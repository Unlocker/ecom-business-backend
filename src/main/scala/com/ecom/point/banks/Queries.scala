package com.ecom.point.banks

import com.ecom.point.banks.Entities.AccessTokenId
import io.getquill._
import com.ecom.point.configs.QuillContext._
import com.ecom.point.banks.Token
import com.ecom.point.banks.TokenDbo._
import com.ecom.point.users.Entities.AccountId


object Queries {
	implicit val tokenSchema: SchemaMeta[TokenDbo] =
		schemaMeta[TokenDbo](
			"token",
			_.id -> "id",
			_.accessToken -> "access_token",
			_.refreshToken -> "refresh_token",
			_.cancelAccessTokenDate -> "cancel_access_token_date",
			_.accountId -> "account_id"
		)
	
	implicit class TokenToDbo(t: Token) {
		def toDbo: TokenDbo =
			TokenDbo(
				id = t.id,
				accessToken = t.accessToken,
				refreshToken = t.refreshToken,
				cancelAccessTokenDate = t.cancelAccessTokenDate,
				accountId = t.accountId
			)
	}
	
	implicit val insert: InsertMeta[TokenDbo] = insertMeta(_.id)
	
	implicit val update: UpdateMeta[TokenDbo] = updateMeta(_.id, _.accountId)
	
	
	def get: EntityQuery[TokenDbo] = quote {
		query[TokenDbo]
	}
	
	def getById(id: AccessTokenId.Type): Quoted[EntityQuery[TokenDbo]] = quote {
		query[TokenDbo]
			.filter(_.id == lift(id))
	}
	
	def getByAccountId(accountId: AccountId.Type): Quoted[EntityQuery[TokenDbo]] = quote {
		query[TokenDbo]
			.filter(_.accountId == lift(accountId))
	}
	
	def create(token: Token): Quoted[ActionReturning[TokenDbo, TokenDbo]] = quote {
		val tokenDbo = token.toDbo
		query[TokenDbo]
			.insertValue(lift(tokenDbo))
			.returning(tk => tk)
	}
	
	def update(token: Token): Quoted[ActionReturning[TokenDbo, TokenDbo]]= quote {
		val tokenDbo = token.toDbo
		query[TokenDbo]
			.filter(_.id == lift(tokenDbo.id))
			.updateValue(lift(tokenDbo))
			.returning(acc => acc)
	}
	
	def delete(id: AccessTokenId.Type): Quoted[ActionReturning[TokenDbo, Index]] = quote {
		query[TokenDbo]
			.filter(_.id == lift(id))
			.delete
			.returning(_ => 1)
	}
	
	
}
