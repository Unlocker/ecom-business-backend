package com.ecom.point.users.repos

import com.ecom.point.configs.QuillContext._
import com.ecom.point.share.entities.UserId
import com.ecom.point.users.models.User
import com.ecom.point.users.models.User.converterFromDbo
import com.ecom.point.utils.RepositoryError
import com.ecom.point.utils.SchemeConverter._
import zio.{IO, Task, ZEnvironment, ZIO, ZLayer}

import javax.sql.DataSource

trait UserRepository {
	def createUser(user: User): IO[RepositoryError, User]
	
	def deleteUser(userId: UserId.Type): IO[RepositoryError, Int]
	
	def getUsers: Task[Seq[User]]
	
	def getUserById(userId: UserId.Type): Task[Option[User]]
	
	def updateUser(user: User): IO[RepositoryError, User]
}

object UserRepository {
	def layer: ZLayer[DataSource, Nothing, UserRepositoryLive] = ZLayer.fromFunction(UserRepositoryLive.apply _)
}

case class UserRepositoryLive(dataSource: DataSource) extends UserRepository {
	private val envDataSource: ZEnvironment[DataSource] = zio.ZEnvironment(dataSource)
	
	override def createUser(user: User): IO[RepositoryError, User] = {
		run(Queries.createUser(user))
			.provideEnvironment(envDataSource)
			.asModelWithMapError(err => RepositoryError(err))
	}
	
	override def deleteUser(userId: UserId.Type): IO[RepositoryError, Int] = {
		run(Queries.deleteUser(userId))
			.provideEnvironment(envDataSource)
			.asModelWithMapError(err => RepositoryError(err))
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
			.asModelWithMapError(err => RepositoryError(err))
	}
}




