package com.ecom.point.share.repos

import com.ecom.point.configs.QuillContext._
import com.ecom.point.share.entities
import com.ecom.point.share.entities.{AccessToken, AccessTokenId, ExpirationTokenDate, RefreshToken, UserId}
import com.ecom.point.share.repos.TokenDbo
import com.ecom.point.users.entities.PhoneNumber
import com.ecom.point.users.models.{User, UserAccessToken}
import com.ecom.point.users.repos.UserDbo._
import io.getquill._
import pdi.jwt.{Jwt, JwtAlgorithm, JwtClaim, JwtTime}
import zio.json._

import java.security.MessageDigest
import java.time.{Clock, Instant}

object Queries {
	implicit val userAccessTokenSchema: SchemaMeta[TokenDbo] = {
		schemaMeta[TokenDbo](
			"user_access_token",
			_.id -> "id",
			_.accessToken -> "access_token",
			_.refreshToken -> "refresh_token",
			_.expirationTokenDate -> "expiration_date",
			_.userId -> "user_id"
		)
	}
	
	implicit class UserAccessTokenToDbo(a: UserAccessToken) {
		def toDbo: TokenDbo =
			TokenDbo(
				id = a.id,
				accessToken = a.accessToken,
				refreshToken = a.refreshToken,
				expirationTokenDate = a.expirationTokenDate,
				userId = a.userId
			)
	}
	
	implicit val insertToken: InsertMeta[TokenDbo] = insertMeta(_.id)
	
	implicit val updateToken: UpdateMeta[TokenDbo] = updateMeta(_.id, _.userId)
	
	private lazy val userTokens = quote{ query[TokenDbo] }
	
	
	def getUserAccessTokenByValue(userAccessToken: AccessToken.Type): Quoted[Query[(TokenDbo, UserDbo)]] =  quote {
		val q = com.ecom.point.users.repos.Queries
		userTokens
			.filter(_.accessToken == lift(userAccessToken))
			.join(q.users).on(_.userId == _.id)
	}
	
	def getUserAccessTokens: Quoted[EntityQuery[TokenDbo]] = quote {
		userTokens
	}
	
	def createUserAccessToken(userAccessToken: UserAccessToken): Quoted[ActionReturning[TokenDbo, TokenDbo]] = quote {
		val tokenDbo = userAccessToken.toDbo
		userTokens
			.insertValue(lift(tokenDbo))
			.returning(t => t)
	}
	
	def getUserAccessTokenById(id: AccessTokenId.Type): Quoted[EntityQuery[TokenDbo]] = quote {
		userTokens
			.filter(_.id == lift(id))
	}
	
	def getUserAccessTokenByUserId(id: UserId.Type): Quoted[EntityQuery[TokenDbo]] = quote {
		userTokens
			.filter(_.userId == lift(id))
	}
	
	def updateUserAccessToken(userAccessToken: UserAccessToken): Quoted[ActionReturning[TokenDbo, TokenDbo]] = quote {
		val tokenDbo = userAccessToken.toDbo
		userTokens
			.filter(_.id == lift(tokenDbo.id))
			.updateValue(lift(tokenDbo))
			.returning(acc => acc)
	}
	
	def deleteUserAceessToken(id: AccessTokenId.Type): Quoted[ActionReturning[TokenDbo, Index]] = quote {
		userTokens
			.filter(_.id == lift(id))
			.delete
			.returning(_ => 1)
	}
	
}


