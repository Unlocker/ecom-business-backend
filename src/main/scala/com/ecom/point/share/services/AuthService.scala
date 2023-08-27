package com.ecom.point.share.services

import com.ecom.point.configs.QuillContext._
import com.ecom.point.share.entities._
import com.ecom.point.share.repos.Queries
import com.ecom.point.users.entities._
import com.ecom.point.users.models.UserAccessToken
import com.ecom.point.utils.SchemeConverter._
import com.ecom.point.utils.{AppError, Unauthorized}
import com.ecom.point.utils.types._
import pdi.jwt.{Jwt, JwtAlgorithm, JwtClaim, JwtTime}
import zio.json._
import zio.prelude._
import zio.{IO, ZEnvironment, ZIO, ZLayer}

import java.security.MessageDigest
import java.time.{Clock, Instant}
import javax.sql.DataSource

trait AuthService {
	def auth(userAccessToken: AccessToken.Type): IO[AppError, AccessToken.Type]
}

object AuthService{
	def layer: ZLayer[DataSource, Nothing, AuthServiceImpl] = ZLayer.fromFunction(AuthServiceImpl.apply _)
}

final case class AuthServiceImpl(dataSource: DataSource) extends AuthService {
	
	private val envDataSource: ZEnvironment[DataSource] = zio.ZEnvironment(dataSource)
	
	private[this] class SecretKey(val value: RefreshToken)
	private[this] object SecretKey {
		def apply(userId: UserId, expirationTokenDate: ExpirationTokenDate): SecretKey = {
			new SecretKey(RefreshToken(
				MessageDigest
					.getInstance("MD5")
					.digest(s"${UserId.unwrap(userId)}${ExpirationTokenDate.unwrap(expirationTokenDate)}".getBytes)
					.toString)
			)
		}
		
		def apply(refreshToken: RefreshToken) = {
			new SecretKey(refreshToken)
		}
	}
	
	private def jwtEncode(phoneNumber: PhoneNumber): SecretKey => (AccessToken, ExpirationTokenDate) = { key =>
		val json = phoneNumber.toJson
		implicit val clock: Clock = Clock.systemUTC()
		val claim = JwtClaim {
			json
		}.issuedNow.expiresIn(300)
		(AccessToken(Jwt.encode(claim, RefreshToken.unwrap(key.value), JwtAlgorithm.HS512)), ExpirationTokenDate(clock.instant()))
	}
	
	private def jwtDecode(userAccessToken: AccessToken): SecretKey => Option[JwtClaim] = { key =>
		Jwt.decode(AccessToken.unwrap(userAccessToken), RefreshToken.unwrap(key.value), Seq(JwtAlgorithm.HS512)).toOption
	}
	
	override def auth(userAccessToken: AccessToken): IO[AppError, AccessToken] = {
		run(Queries.getUserAccessTokenByValue(userAccessToken))
			.provideEnvironment(envDataSource)
			.map(_.headOption)
			.flatMap { tokenWithUser =>
				tokenWithUser match {
					case None => ZIO.fail(Unauthorized())
					case Some((token, user)) => {
						if (token.expirationTokenDate < ExpirationTokenDate(Instant.now())) {
							val clock: Clock = Clock.systemUTC()
							val exAccess = jwtDecode(token.accessToken)(SecretKey(token.refreshToken)).flatMap(_.expiration)
							if (exAccess.exists(_ < JwtTime.nowSeconds(clock))) {
								ZIO.attempt(token.accessToken).orElseFail(Unauthorized())
							} else {
								val newAccessToken = jwtEncode(user.phoneNumber)(SecretKey(token.refreshToken))
								val newToken = Seq(token.copy(accessToken = newAccessToken._1)).asModel(UserAccessToken.converterFromDbo)
								run(Queries.updateUserAccessToken(newToken.head))
									.provideEnvironment(envDataSource)
									.mapBoth(_ => Unauthorized(), _ => newAccessToken._1)
							}
						} else {
							ZIO.fail(Unauthorized())
						}
					}
				}
			}.orElseFail(Unauthorized())
	}
}
