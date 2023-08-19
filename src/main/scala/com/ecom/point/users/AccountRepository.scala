package com.ecom.point.users

import com.ecom.point.configs.QuillContext._
import com.ecom.point.utils.SchemeConverter._
import Account._
import com.ecom.point.users.Entities.AccountId
import com.ecom.point.utils.Errors.RepositoryError
import zio.{IO, Task, ZEnvironment, ZLayer}

import javax.sql.DataSource

trait AccountRepository {
	def createAccount(account: Account): IO[RepositoryError, Account]
	def deleteAccount(accountId: AccountId.Type): IO[RepositoryError, Int]
	def getAccounts: Task[Seq[Account]]
	def getAccountById(accountId: AccountId.Type): Task[Option[Account]]
	def updateAccount(account: Account): IO[RepositoryError, Account]
}

object AccountRepository {
	def layer = ZLayer.fromFunction(AccountRepositoryImpl.apply _)
}

case class AccountRepositoryImpl(dataSource: DataSource) extends AccountRepository {
	private val envDataSource: ZEnvironment[DataSource] = zio.ZEnvironment(dataSource)
	
	override def createAccount(account: Account): IO[RepositoryError, Account] = {
		run(Queries.create(account))
			.provideEnvironment(envDataSource)
			.asModelWithMapError(err => RepositoryError(err.getErrorCode, err.getMessage))
	}
	
	override def deleteAccount(accountId: AccountId.Type): IO[RepositoryError, Int] = {
		run(Queries.delete(accountId))
			.provideEnvironment(envDataSource)
			.asModelWithMapError(err => RepositoryError(err.getErrorCode, err.getMessage))
	}
	
	override def getAccounts: Task[Seq[Account]] = {
		run(Queries.get)
			.provideEnvironment(envDataSource)
			.asModel
	}
	
	override def getAccountById(accountId: AccountId.Type): Task[Option[Account]] = {
		run(Queries.getById(accountId))
			.provideEnvironment(envDataSource)
			.map(_.headOption)
			.asModel
	}
	
	override def updateAccount(account: Account): IO[RepositoryError, Account] = {
		run(Queries.update(account))
			.provideEnvironment(envDataSource)
			.asModelWithMapError(err => RepositoryError(err.getErrorCode, err.getMessage))
	}
}
