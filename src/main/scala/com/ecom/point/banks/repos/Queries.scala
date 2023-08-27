package com.ecom.point.banks.repos

import com.ecom.point.banks.models.BankAccessToken
import com.ecom.point.configs.QuillContext._
import com.ecom.point.share.entities._
import com.ecom.point.share.repos.TokenDbo
import io.getquill._

object Queries {
	implicit val tokenSchema: SchemaMeta[TokenDbo] =
		schemaMeta[TokenDbo](
			"bank_access_token",
			_.id -> "id",
			_.accessToken -> "access_token",
			_.refreshToken -> "refresh_token",
			_.expirationTokenDate -> "expiration_date",
			_.userId -> "user_id"
		)
	
	implicit class TokenToDbo(t: BankAccessToken) {
		def toDbo: TokenDbo =
			TokenDbo(
				id = t.id,
				accessToken = t.accessToken,
				refreshToken = t.refreshToken,
				expirationTokenDate = t.expirationTokenDate,
				userId = t.userId
			)
	}
	
	implicit val insert: InsertMeta[TokenDbo] = insertMeta(_.id)

	implicit val update: UpdateMeta[TokenDbo] = updateMeta(_.id, _.userId)
	
	
	def getBankAccessTokens: Quoted[EntityQuery[TokenDbo]] = quote(
		query[TokenDbo]
	)
	
	def getBankAccessTokenById(id: AccessTokenId.Type): Quoted[EntityQuery[TokenDbo]] = quote (
		query[TokenDbo]
			.filter(_.id == lift(id))
	)
	
	def getBankAccessTokenByUserId(userId: UserId.Type): Quoted[EntityQuery[TokenDbo]] = quote (
		query[TokenDbo]
			.filter(_.userId == lift(userId))
	)
	
	def createBankAccessToken(bankAccessToken: BankAccessToken): Quoted[ActionReturning[TokenDbo, TokenDbo]] = {
		val tokenDbo = bankAccessToken.toDbo
		quote(
			query[TokenDbo]
				.insertValue(lift(tokenDbo))
				.returning(tk => tk)
		)
	}
	
	def updateBankAccessToken(bankAccessToken: BankAccessToken): Quoted[ActionReturning[TokenDbo, TokenDbo]] =  {
		val tokenDbo = bankAccessToken.toDbo
		quote(
			query[TokenDbo]
				.filter(_.id == lift(tokenDbo.id))
				.updateValue(lift(tokenDbo))
				.returning(acc => acc)
		)
	}
	
	def deleteBankAccessToken(id: AccessTokenId.Type): Quoted[ActionReturning[TokenDbo, Index]] = quote (
		query[TokenDbo]
			.filter(_.id == lift(id))
			.delete
			.returning(_ => 1)
	)
	
	
}
