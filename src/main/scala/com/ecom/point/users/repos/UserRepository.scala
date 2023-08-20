package com.ecom.point.users.repos

import com.ecom.point.configs.QuillContext._
import com.ecom.point.share.entities.UserId
import com.ecom.point.users.models.User
import com.ecom.point.utils.Errors.RepositoryError
import com.ecom.point.utils.SchemeConverter._
import zio.{IO, Task, ZEnvironment, ZLayer}

import javax.sql.DataSource

trait AccountRepository {
	def createUser(user: User): IO[RepositoryError, User]
	
	def deleteUser(userId: UserId.Type): IO[RepositoryError, Int]
	
	def getUsers: Task[Seq[User]]
	
	def getUserById(userId: UserId.Type): Task[Option[User]]
	
	def updateUser(user: User): IO[RepositoryError, User]
}

object AccountRepository {
	def layer = ZLayer.fromFunction(AccountRepositoryImpl.apply _)
}

case class AccountRepositoryImpl(dataSource: DataSource) extends AccountRepository {
	private val envDataSource: ZEnvironment[DataSource] = zio.ZEnvironment(dataSource)
	
	override def createUser(user: User): IO[RepositoryError, User] = {
		run(Queries.createUser(user))
			.provideEnvironment(envDataSource)
			.asModelWithMapError(err => RepositoryError(err.getErrorCode, err.getMessage))
	}
	
	override def deleteUser(userId: UserId.Type): IO[RepositoryError, Int] = {
		run(Queries.deleteUser(userId))
			.provideEnvironment(envDataSource)
			.asModelWithMapError(err => RepositoryError(err.getErrorCode, err.getMessage))
	}
	
	override def getUsers: Task[Seq[User]] = {
		run(Queries.getUsers)
			.provideEnvironment(envDataSource)
			.asModel
	}
	
	override def getUserById(userId: UserId.Type): Task[Option[User]] = {
		run(Queries.getUserById(userId))
			.provideEnvironment(envDataSource)
			.map(_.headOption)
			.asModel
	}
	
	override def updateUser(user: User): IO[RepositoryError, User] = {
		run(Queries.updateUser(user))
			.provideEnvironment(envDataSource)
			.asModelWithMapError(err => RepositoryError(err.getErrorCode, err.getMessage))
	}
}
