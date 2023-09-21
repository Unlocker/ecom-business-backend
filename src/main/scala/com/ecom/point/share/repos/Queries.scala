package com.ecom.point.share.repos

import com.ecom.point.configs.QuillContext._
import com.ecom.point.share.types._
import com.ecom.point.users.models.UserAccessToken
import com.ecom.point.users.repos.UserDbo
import io.getquill._

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
	
	private lazy val userTokens = quote(query[TokenDbo])
	
	
	def getUserAccessTokenWithUserByValue(userAccessToken: AccessToken): Quoted[Query[(TokenDbo, UserDbo)]] = {
		val userQueries = com.ecom.point.users.repos.Queries
		quote(
			userTokens
				.filter(_.accessToken == lift(userAccessToken))
				.join(userQueries.users).on(_.userId == _.id)
		)
	}
	
	def getUserAccessTokenByValue(accessToken: AccessToken) = {
		quote(
			userTokens.filter(_.accessToken == lift(accessToken))
		)
	}
	
	def getUserAccessTokens: Quoted[EntityQuery[TokenDbo]] = quote(
		userTokens
	)
	
	def createOrUpdateUserAccessToken(userAccessToken: UserAccessToken): Quoted[ActionReturning[TokenDbo, TokenDbo]] = {
		val tokenDbo = userAccessToken.toDbo
		quote(
			userTokens
				.insertValue(lift(tokenDbo))
				.onConflictUpdate(_.userId)((e, n) => e.accessToken -> n.accessToken, (e, n) => e.refreshToken -> n.refreshToken, (e, n) => e.expirationTokenDate -> n.expirationTokenDate)
				.returning(t => t)
		)
	}
	
	def getUserAccessTokenById(id: AccessTokenId.Type): Quoted[EntityQuery[TokenDbo]] = quote(
		userTokens
			.filter(_.id == lift(id))
	)
	
	def getUserAccessTokenByUserId(id: UserId.Type): Quoted[EntityQuery[TokenDbo]] = quote(
		userTokens
			.filter(_.userId == lift(id))
	)
	
	def updateUserAccessToken(userAccessToken: UserAccessToken): Quoted[ActionReturning[TokenDbo, TokenDbo]] = {
		val tokenDbo = userAccessToken.toDbo
		quote(
			userTokens
				.filter(_.userId == lift(tokenDbo.userId))
				.updateValue(lift(tokenDbo))
				.returning(acc => acc)
		)
	}
	
	def deleteUserAceessToken(id: AccessTokenId.Type): Quoted[ActionReturning[TokenDbo, Index]] = quote(
		userTokens
			.filter(_.id == lift(id))
			.delete
			.returning(_ => 1)
	)
	
}


