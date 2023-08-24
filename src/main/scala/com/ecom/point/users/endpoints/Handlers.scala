package com.ecom.point.users.endpoints

import com.ecom.point.Middlewares
import com.ecom.point.users.endpoints.EndpointData.{SignInUpResponse, SignUpRequest, SignUpRequest}
import com.ecom.point.users.services.UserService
import zio.{ZIO, ZNothing, http}
import zio.http.RequestHandlerMiddlewares
import zio.http.codec.HttpCodec._
import zio.http.endpoint.EndpointMiddleware.None
import zio.http.endpoint._

object Handlers {
	val signUp: Routes[UserService, ZNothing, None] = Endpoint
		.post(literal("sign-up"))
		.in[SignUpRequest]
		.out[SignInUpResponse]
		.implement{ req =>
			for {
				service <- ZIO.service[UserService]
				resp <- service.signUp(req).orDie
			} yield SignInUpResponse(resp)
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
	val authApi = signUp.toApp[UserService]
}

