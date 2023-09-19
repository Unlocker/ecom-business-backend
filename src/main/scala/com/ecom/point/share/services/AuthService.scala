package com.ecom.point.share.services

import com.ecom.point.share.types._
import com.ecom.point.share.repos.Queries
import com.ecom.point.users.endpoints.EndpointData.{SignInRequest, SignUpRequest}
import com.ecom.point.users.models.{User, UserAccessToken}
import com.ecom.point.users.repos.{UserDbo, UserRepository}
import com.ecom.point.users.services.UserService
import com.ecom.point.utils.SchemeConverter._
import com.ecom.point.utils.{AppError, Unauthorized}
import io.getquill.SnakeCase
import io.getquill.jdbczio.Quill
import pdi.jwt.{Jwt, JwtAlgorithm, JwtClaim, JwtTime}
import zio.http.Header
import zio.http.Header.Authorization.Bearer
import zio.json._
import zio.json.ast.Json
import zio.prelude._
import zio.{&, IO, Task, ZIO, ZLayer}

import java.security.MessageDigest
import java.time.{Clock, Instant}
import java.util.UUID

trait AuthService {
	def verify(inputHeader: Option[Header.Authorization]): Task[Option[Header.Authorization]]
	def signUp(signUpRequest: SignUpRequest, salt: Salt): IO[Exception, Int]
	def signIn(signInRequest: SignInRequest): Task[Option[AccessToken]]
	def auth(userAccessToken: AccessToken): IO[AppError, (AccessToken, User)]
	
}

object AuthService{
	def layer: ZLayer[Quill.Postgres[SnakeCase] & UserService, Nothing, AuthServiceImpl] = ZLayer.fromFunction(AuthServiceImpl.apply _)
}

final case class AuthServiceImpl(dataSource: Quill.Postgres[SnakeCase], userService: UserService) extends AuthService {
	
	import dataSource._
	import AuthTokenHelper._
	
	override def signUp(signUpRequest: SignUpRequest, salt: Salt): IO[Exception, Index] = {
		val user = User(
			id = UserId(UUID.randomUUID()),
			phoneNumber = signUpRequest.phoneNumber,
			name = signUpRequest.name,
			password = signUpRequest.password,
			activateDate = None,
			blockDate = None,
			createdAt = CreatedDate(Instant.now()),
			lastLoginDate = None
		)
		userService.addUser(user, salt)
	}
	
	override def verify(inputHeader: Option[Header.Authorization]): Task[Option[Header.Authorization]] = {
		inputHeader match {
			case Some(Bearer(token)) => auth(token).map(_.map(value => Header.Authorization.Bearer(value.unwrap)))
			case None => ZIO.succeed(Option.empty[Header.Authorization])
		}
	}
	
	private def auth(tokenValue: String): IO[AppError, Option[AccessToken]] = {
		run(Queries.getUserAccessTokenByValue(AccessToken(tokenValue)))
			.map(_.headOption)
			.flatMap { token =>
				println(token)
				token match {
					case None => ZIO.fail(Unauthorized())
					case Some(token) => {
						if (token.expirationTokenDate < ExpirationTokenDate(Instant.now())) {
							val clock: Clock = Clock.systemUTC()
							println(clock)
							val decodedToken = jwtDecode(token.accessToken)(SecretKey(token.refreshToken))
							println(decodedToken)
							val exAccess = decodedToken.flatMap(_.expiration)
							println(exAccess)
							val phoneNumber = decodedToken.flatMap(x => x.content.fromJson[AuthTokenHelper.PhoneNumberJwt].map(_.phone).toOption)
							println(phoneNumber)
							if (exAccess.exists(_ < JwtTime.nowSeconds(clock))) {
								ZIO.attempt(Option(token.accessToken)).orElseFail(Unauthorized())
							} else {
								val newAccessToken = jwtEncode(phoneNumber.get)(SecretKey(token.refreshToken))
								val newToken = Seq(token.copy(accessToken = newAccessToken._1)).asModel(UserAccessToken.converterFromDbo)
								run(Queries.updateUserAccessToken(newToken.head))
									.mapBoth(_ => Unauthorized(), _ => Option(newAccessToken._1))
							}
						} else {
							println("SSSSSSSSSSS")
							ZIO.fail(Unauthorized())
						}
					}
				}
			}
	}.orElseFail(Unauthorized())
	
	override def signIn(signInRequest: SignInRequest): Task[Option[AccessToken]] = {
		def generateAcccesToken(maybeUser: Option[User]): Task[Option[AccessToken]] = {
			maybeUser match {
				case None => ZIO.attempt(Option.empty[AccessToken])
				case Some(valueUser) => {
					val clock: Clock = Clock.systemUTC()
					val refreshTokenKey = SecretKey(valueUser.id, ExpirationTokenDate(clock.instant()))
					val token = jwtEncode(valueUser.phoneNumber)(refreshTokenKey)
					val userToken = UserAccessToken(
						id = AccessTokenId(UUID.randomUUID()),
						accessToken = token._1,
						refreshToken = refreshTokenKey.value,
						expirationTokenDate = token._2,
						userId = valueUser.id
					)
					run(Queries.createUserAccessToken(userToken))
						.map { a => Option(a.accessToken)}
				}
			}
		}
		
		for{
			userMaybe <- userService.findUserByPhoneAndPassword(signInRequest.phoneNumber, signInRequest.password)
			token <- generateAcccesToken(userMaybe)
		} yield token
	}


	
	override def auth(userAccessToken: AccessToken): IO[AppError, (AccessToken, User)] = {
		run(Queries.getUserAccessTokenWithUserByValue(userAccessToken))
			.map(_.headOption)
			.flatMap { tokenWithUser =>
				tokenWithUser match {
					case None => ZIO.fail(Unauthorized())
					case Some((token, user)) => {
						if (token.expirationTokenDate < ExpirationTokenDate(Instant.now())) {
							val clock: Clock = Clock.systemUTC()
							val exAccess = jwtDecode(token.accessToken)(SecretKey(token.refreshToken)).flatMap(_.expiration)
							if (exAccess.exists(_ < JwtTime.nowSeconds(clock))) {
								ZIO.attempt((token.accessToken, User.converterFromDbo(user))).orElseFail(Unauthorized())
							} else {
								val newAccessToken = jwtEncode(user.phoneNumber)(SecretKey(token.refreshToken))
								val newToken = Seq(token.copy(accessToken = newAccessToken._1)).asModel(UserAccessToken.converterFromDbo)
								run(Queries.updateUserAccessToken(newToken.head))
									.mapBoth(_ => Unauthorized(), _ => (newAccessToken._1, User.converterFromDbo(user)))
							}
						} else {
							ZIO.fail(Unauthorized())
						}
					}
				}
			}
	}.orElseFail(Unauthorized())
}


object AuthTokenHelper{
	case class PhoneNumberJwt(phone: PhoneNumber)
	object PhoneNumberJwt {
		implicit val jsonJwtPhon: JsonCodec[PhoneNumberJwt] = DeriveJsonCodec.gen[PhoneNumberJwt]
	}
	
	class SecretKey(val value: RefreshToken)
	
	object SecretKey {
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
	
	def jwtEncode(phoneNumber: PhoneNumber): SecretKey => (AccessToken, ExpirationTokenDate) = { key =>
		val json = PhoneNumberJwt(phoneNumber).toJson
		implicit val clock: Clock = Clock.systemUTC()
		val claim = JwtClaim {
			json
		}.issuedNow.expiresIn(300)
		(AccessToken(Jwt.encode(claim, RefreshToken.unwrap(key.value), JwtAlgorithm.HS512)), ExpirationTokenDate(clock.instant().plusSeconds(300)))
	}
	
	def jwtDecode(userAccessToken: AccessToken): SecretKey => Option[JwtClaim] = { key =>
		Jwt.decode(AccessToken.unwrap(userAccessToken), RefreshToken.unwrap(key.value), Seq(JwtAlgorithm.HS512)).toOption
	}
}
