package com.ecom.point.users.repos

import com.ecom.point.share.types._
import com.ecom.point.users.models.User
import io.getquill._
import com.ecom.point.configs.QuillContext._

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
			_.lastLoginDate -> "last_login_date",
			_.salt -> "salt"
		)
	
	implicit class UserToDbo(a: User) {
		def toDbo: UserDbo = {
			UserDbo(
				id = a.id,
				phoneNumber = a.phoneNumber,
				name = a.name,
				password = a.password,
				activateDate = a.activateDate,
				createdAt = a.createdAt,
				blockDate = a.blockDate,
				lastLoginDate = a.lastLoginDate,
				salt = Salt("")
			)
		}
	}
	
	def withSalt(salt: Salt): UserDbo => UserDbo = dbo => dbo.copy(salt = salt)
	
//	implicit val insertUser: InsertMeta[UserDbo] = insertMeta(_.id, _.createdAt)
//
//	implicit val updateUser: UpdateMeta[UserDbo] = updateMeta(_.id, _.phoneNumber, _.createdAt)
	
	
	lazy val users: Quoted[EntityQuery[UserDbo]] = quote (
		query[UserDbo]
	)
	
	implicit class RightQuery[T](q: Query[T]) {
		def rightSymbolPhone(phoneNumber: PhoneNumber) = quote(sql"$q RIGHT(${phoneNumber}, ${11})".as[Query[T]])
	}
	
	
	def getUsers: Quoted[EntityQuery[UserDbo]] = quote (users)

	
	def getUserById(id: UserId.Type): Quoted[EntityQuery[UserDbo]] =
		quote (
			users
				.filter(_.id == lift(id))
		)
	
	def findUserByPhoneNumber(phoneNumber: PhoneNumber) =
		quote{
			users.filter(u => u.phoneNumber == lift(phoneNumber))
		}
	
	def itsPhoneNotUsed(phoneNumber: PhoneNumber): Quoted[Boolean] =
		quote(
			query[UserDbo]
				.filter(u =>
					sql"RIGHT(${u.phoneNumber}, ${10})".pure.as[PhoneNumber] == lift(PhoneNumber(phoneNumber.unwrap.takeRight(10)))
				).isEmpty
		)
	
	def createUser(user: User, salt: Salt): Quoted[ActionReturning[UserDbo, Index]] = {
		val userDbo = withSalt(salt)(user.toDbo)
		quote (
			users
				.insertValue(lift(userDbo))
				 .returning(_ => 1)
			)
	 }
	
	def updateUser(account: User): Quoted[ActionReturning[UserDbo, UserDbo]] = {
		val userDbo = account.toDbo
		quote(
			users
			.filter(_.id == lift(userDbo.id))
			.updateValue(lift(userDbo))
			.returning(acc => acc)
		)
	}
	
	def deleteUser(accountId: UserId.Type): Quoted[ActionReturning[UserDbo, Index]] =
		quote (
			users
				.filter(_.id == accountId)
				.delete
				.returning(_ => 1)
		)
	}
