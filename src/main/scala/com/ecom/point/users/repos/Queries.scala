package com.ecom.point.users.repos

import com.ecom.point.configs.QuillContext.{Index, lift, quote, _}
import com.ecom.point.share.entities.{AccessTokenId, UserId}
import com.ecom.point.share.repos.TokenDbo
import com.ecom.point.users.models.{User, UserAccessToken}
import com.ecom.point.users.repos.UserDbo._
import io.getquill._


object Queries {
	implicit val userSchema: SchemaMeta[UserDbo] =
		schemaMeta[UserDbo](
			"users",
			_.id -> "id",
			_.phoneNumber -> "phone_number",
			_.name -> "name",
			_.password -> "password",
			_.activateDate -> "activate_date",
			_.createdAt -> "created_at",
			_.blockDate -> "block_date",
			_.lastLoginDate -> "last_login_date"
		)
	
	implicit class UserToDbo(a: User) {
		def toDbo: UserDbo =
			UserDbo(
				id = a.id,
				phoneNumber = a.phoneNumber,
				name = a.name,
				password = a.password,
				activateDate = a.activateDate,
				createdAt = a.createdAt,
				blockDate = a.blockDate,
				lastLoginDate = a.lastLoginDate
			)
	}
	
	implicit val insertUser: InsertMeta[UserDbo] = insertMeta(_.id, _.createdAt)
	
	implicit val updateUser: UpdateMeta[UserDbo] = updateMeta(_.id, _.phoneNumber, _.createdAt)
	
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
	
	lazy val users = quote {
		query[UserDbo]
	}
	
	lazy val userTokens = quote {
		query[TokenDbo]
	}
	
	def getUsers: EntityQuery[UserDbo] = quote {
		users
	}
	
	def getUserById(id: UserId.Type): Quoted[EntityQuery[UserDbo]] = quote {
		users
			 .filter(_.id == lift(id))
	 }
	
	def createUser(user: User): Quoted[ActionReturning[UserDbo, UserDbo]] = quote {
		val userDbo = user.toDbo
		users
			.insertValue(lift(userDbo))
			 .returning(acc => acc)
	 }
	
	def updateUser(account: User): Quoted[ActionReturning[UserDbo, UserDbo]] = quote {
		val userDbo = account.toDbo
		users
			.filter(_.id == lift(userDbo.id))
			.updateValue(lift(userDbo))
			 .returning(acc => acc)
	 }
	
	def deleteUser(accountId: UserId.Type): Quoted[ActionReturning[UserDbo, Index]] = quote {
		users
			 .filter(_.id == accountId)
			 .delete
			 .returning(_ => 1)
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
	
	def getUserAccessTokenById(id: AccessTokenId.Type) = quote {
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
	
	def deleteUserAceessToken(id: AccessTokenId.Type): Quoted[ActionReturning[UserDbo, Index]] = quote {
		users
			.filter(_.id == lift(id))
			.delete
			.returning(_ => 1)
	}
	
	
	
	
}
