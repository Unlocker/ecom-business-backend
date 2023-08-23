package com.ecom.point.users.services


import com.ecom.point.share.entities.{AccessToken, UserId}
import com.ecom.point.users.endpoints.EndpointData.SignUpRequest
import com.ecom.point.users.entities.CreatedDate
import com.ecom.point.users.models.User
import com.ecom.point.users.repos.UserRepository
import com.ecom.point.utils.AppError
import zio.{IO, ZIO, ZLayer}

import java.time.Instant
import java.util.UUID





trait UserService {
	def signUp(signUpRequest: SignUpRequest): IO[AppError, AccessToken.Type]
}

object UserService {
	def signUp(signUpRequest: SignUpRequest) = ZIO.serviceWithZIO[UserService](_.signUp(signUpRequest))
}
case class UserServiceLive(userRepository: UserRepository) extends UserService{
	override def signUp(signUpRequest: SignUpRequest): IO[AppError, AccessToken.Type] =  ???
//	{
//		val user = User(
//			id = UserId(UUID.randomUUID()),
//			phoneNumber = signUpRequest.phoneNumber,
//			name = signUpRequest.name,
//			password = signUpRequest.password,
//			activateDate = None,
//			blockDate = None,
//			createdAt = CreatedDate(Instant.now()),
//			lastLoginDate = None
//		)
//		for{
//			createdUser <- userRepository.createUser(user)
//			addedToken <- authService.getToken(created)
//		} yield ()
//
//	}
}

object UserServiceLive {
	def layer: ZLayer[UserRepository, Nothing, UserServiceLive] = ZLayer.fromFunction(UserServiceLive.apply _)
}
