package com.ecom.point.users

import io.getquill._
import com.ecom.point.configs.QuillContext._
import com.ecom.point.users.AccountDbo._
import com.ecom.point.users.Entities.AccountId


object Queries {
	implicit val accountSchema: SchemaMeta[AccountDbo] =
		schemaMeta[AccountDbo](
			"account",
			_.id -> "id",
			_.phoneNumber -> "phone_number",
			_.name -> "name",
			_.password -> "password",
			_.activateDate -> "activate_date",
			_.createdAt -> "created_at",
			_.blockDate -> "block_date",
			_.lastLoginDate -> "last_login_date"
		)
	
	implicit class AccountToDbo(a: Account) {
		def toDbo: AccountDbo =
			AccountDbo(
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
		
	implicit val insert: InsertMeta[AccountDbo] = insertMeta(_.id, _.createdAt)
	
	implicit val update: UpdateMeta[AccountDbo] = updateMeta(_.id, _.phoneNumber, _.createdAt)
	
		
	def get: EntityQuery[AccountDbo] = quote {
		query[AccountDbo]
	}
	
	 def getById(id: AccountId.Type): Quoted[EntityQuery[AccountDbo]] = quote {
		 query[AccountDbo]
			 .filter(_.id == lift(id))
	 }
	
	 def create(account: Account): Quoted[ActionReturning[AccountDbo, AccountDbo]] = quote {
		 val accountDbo = account.toDbo
		 query[AccountDbo]
			 .insertValue(lift(accountDbo))
			 .returning(acc => acc)
	 }
	
	 def update(account: Account): Quoted[ActionReturning[AccountDbo, AccountDbo]] = quote {
		 val accountDbo = account.toDbo
		 query[AccountDbo]
			 .filter(_.id == lift(accountDbo.id))
			 .updateValue(lift(accountDbo))
			 .returning(acc => acc)
	 }
	
	 def delete(accountId: AccountId.Type): Quoted[ActionReturning[AccountDbo, Index]] = quote {
		 query[AccountDbo]
			 .filter(_.id == accountId)
			 .delete
			 .returning(_ => 1)
	 }
	
	
}
