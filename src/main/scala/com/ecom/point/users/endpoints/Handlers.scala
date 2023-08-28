package com.ecom.point.users.endpoints

import com.ecom.point.share.types.AccessToken
import com.ecom.point.users.endpoints.EndpointData.{SignInUpResponse, SignUpRequest}
import com.ecom.point.users.services.UserService
import zio.http.MediaType
import zio.http.codec.ContentCodec
import zio.http.codec.HttpCodec._
import zio.http.endpoint.EndpointMiddleware.None
import zio.http.endpoint._
import zio.{ZIO, ZNothing, http}

object Handlers {
	
	val signUp: Routes[UserService, ZNothing, None] = Endpoint
		.post(literal("sign-up"))
		.inCodec(ContentCodec.content[SignUpRequest](MediaType.application.json))
		.outCodec(ContentCodec.content[SignInUpResponse](MediaType.application.json))
		.implement{ req =>
			ZIO.succeed(SignInUpResponse(AccessToken("SSSSSSSSSS")))
//			ZIO.serviceWithZIO[UserService](_.signUp(req)).orDie.map(data => SignInUpResponse(data))
		}
	
	
	
	//	val signIn = Endpoint
//		.post(literal("sign-in"))
//		.in[SignUpRequest]
//		.out[SignInUpResponse]
//		.implement { req =>
//			for {
//				service <- ZIO.service[UserService]
//				resp <- service.signIn(req)
//			} yield resp
//		}.toApp
//
//	val signOff = Endpoint
//		.get(literal("sign-off"))
//		.out[Int](zio.http.Status.NoContent)
//		.implement { reg =>
//			for {
//				service <- ZIO.service[UserService]
//				resp <- service.signOff(req)
//			} yield resp
//		}.toApp
	
//	val userEndpoints = (signUp ++ signIn) @@ Middlewares.authorization ++ signOff @@ Middlewares.authentification
//	val authApi = signUp.toApp[UserService]
val authApi: http.App[UserService] = signUp.toApp
}

