package com.ecom.point.users.services


import com.ecom.point.share.types._
import com.ecom.point.users.endpoints.EndpointData.SignUpRequest
import com.ecom.point.users.models.User
import com.ecom.point.users.repos.UserRepository
import com.ecom.point.utils.AppError
import zio.{IO, Task, ZLayer}

import java.time.Instant
import java.util.UUID





trait UserService {
	def signUp(signUpRequest: SignUpRequest): IO[Exception, Int]
	def getUserById(id: UserId): Task[Option[User]]
}

object UserService {
	def layer: ZLayer[UserRepository, Nothing, UserServiceLive] = ZLayer.fromFunction(UserServiceLive.apply _)
}
case class UserServiceLive(userRepository: UserRepository) extends UserService{
	override def signUp(signUpRequest: SignUpRequest): IO[Exception, Int] = {
		val user = User(
			id = UserId(UUID.randomUUID()),
			phoneNumber = signUpRequest.phoneNumber,
			name = signUpRequest.name,
			password = signUpRequest.password,
			activateDate = None,
			blockDate = None,
			createdAt = CreatedDate(Instant.now()),
			lastLoginDate = None
		)
		userRepository.createUser(user)
	}
	
	override def getUserById(id: UserId): Task[Option[User]] = {
		userRepository.getUserById(id)
	}
}


