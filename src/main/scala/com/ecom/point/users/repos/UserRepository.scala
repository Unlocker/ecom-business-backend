package com.ecom.point.users.repos

import com.ecom.point.share.types._
import com.ecom.point.users.models.User
import com.ecom.point.users.models.User.converterFromDbo
import com.ecom.point.utils.RepositoryError
import com.ecom.point.utils.SchemeConverter._
import io.getquill.SnakeCase
import io.getquill.jdbczio.Quill
import zio.{IO, Task, ZIO, ZLayer}


trait UserRepository {
	def createUser(user: User): IO[Exception, Int]
	
	def deleteUser(userId: UserId.Type): IO[RepositoryError, Int]
	
	def getUsers: Task[Seq[User]]
	
	def getUserById(userId: UserId.Type): Task[Option[User]]
	
	def updateUser(user: User): IO[RepositoryError, User]
}

object UserRepository {
	def layer: ZLayer[Quill.Postgres[SnakeCase], Nothing, UserRepositoryLive] = ZLayer.fromFunction(UserRepositoryLive.apply _)
}

case class UserRepositoryLive(dataSource: Quill.Postgres[SnakeCase]) extends UserRepository {
	
	import dataSource._

	override def createUser(user: User): IO[Exception, Int] = {
		run(Queries.itsPhoneNotUsed(user.phoneNumber)).flatMap{ isFree =>
			if (isFree) {
				println(isFree)
				run(Queries.createUser(user)).asModelWithMapError(err => RepositoryError(err))
			} else {
					ZIO.fail[RepositoryError](RepositoryError.PhoneNumberMustBeUnique(23505, "Указанный номер зарегистрирован на другое лицо в нашей системе"))
						.mapBoth(err => err, _ => 0)
			}
		}
	}
	
	override def deleteUser(userId: UserId): IO[RepositoryError, Int] = {
		run(Queries.deleteUser(userId))
			.asModelWithMapError(err => RepositoryError(err))
	}
	
	override def getUsers: Task[Seq[User]] = {
		run(Queries.getUsers)
			.asModel
	}
	
	override def getUserById(userId: UserId): Task[Option[User]] = {
		run(Queries.getUserById(userId))
			.map(_.headOption)
			.asModel
	}
	
	override def updateUser(user: User): IO[RepositoryError, User] = {
		run(Queries.updateUser(user))
			.asModelWithMapError(err => RepositoryError(err))
	}
}




