package com.ecom.point.users.services


import com.ecom.point.share.types._
import com.ecom.point.users.endpoints.EndpointData.SignUpRequest
import com.ecom.point.users.models.User
import com.ecom.point.users.repos.UserRepository
import com.ecom.point.utils.AppError
import zio.{IO, Task, ZIO, ZLayer}

import java.time.Instant
import java.util.UUID


trait UserService {
	def addUser(user: User, salt: Salt): IO[Exception, Int]
	def getUserById(id: UserId): Task[Option[User]]
	def findUserByPhoneAndPassword(phoneNumber: PhoneNumber, password: Password): Task[Option[User]]
}

object UserService {
	def layer: ZLayer[UserRepository, Nothing, UserServiceLive] = ZLayer.fromFunction(UserServiceLive.apply _)
}
case class UserServiceLive(userRepository: UserRepository) extends UserService{
	override def addUser(user: User, salt: Salt): IO[Exception, Int] = {
		userRepository.createUser(user, salt)
	}
	
	override def getUserById(id: UserId): Task[Option[User]] = {
		userRepository.getUserById(id)
	}
	
	override def findUserByPhoneAndPassword(phoneNumber: PhoneNumber, password: Password): Task[Option[User]] = {
		userRepository.findUserByPhoneAndPassword(phoneNumber, password)
	}
}


