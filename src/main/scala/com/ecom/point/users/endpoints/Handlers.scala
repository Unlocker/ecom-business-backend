package com.ecom.point.users.endpoints

import com.ecom.point.AuthMiddleware
import com.ecom.point.share.services.AuthService
import com.ecom.point.share.types.AccessToken
import com.ecom.point.users.endpoints.EndpointData.{ApiError, PhoneAlreadyUsed, SignInUpResponse, SignUpRequest}
import com.ecom.point.users.services.UserService
import zio.http.{Handler, Http, MediaType, Request, Response}
import zio.http.codec.{ContentCodec, HttpCodec, TextCodec}
import zio.http.codec.HttpCodec._
import zio.http.endpoint.EndpointMiddleware.None
import zio.http.endpoint._
import zio.http.{Status => StatusResponse}
import zio.{ZIO, ZNothing, http}

object Handlers {
	
	val signUp: Routes[UserService, ApiError, None] = Endpoint
		.post(path = "api" / "v1" / "user" / "sign-up")
		.inCodec(ContentCodec.content[SignUpRequest](MediaType.application.json))
		.out[Unit]
		.outError[PhoneAlreadyUsed](StatusResponse.Conflict)
		.implement{ req =>
			ZIO.serviceWithZIO[UserService](_.signUp(req)).mapBoth(_ => PhoneAlreadyUsed(), _ => ZIO.unit)
		}
	
//	val signIn = Endpoint
//		.post(path ++ literal("sign-in"))
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
val authApi = signUp.toApp //++ signIn ++ AuthMiddleware.secure(signOut.toApp)
}

