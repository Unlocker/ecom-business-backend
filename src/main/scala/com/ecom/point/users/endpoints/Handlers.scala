package com.ecom.point.users.endpoints

import com.ecom.point.AuthMiddleware
import com.ecom.point.share.services.AuthService
import com.ecom.point.share.types.{AccessToken, Password, PhoneNumber}
import com.ecom.point.users.endpoints.EndpointData.{ApiError, PasswordsNotEqual, PasswordsOrPhoneIncorrect, PhoneAlreadyUsed, SignInRequest, SignUpRequest}
import com.ecom.point.users.services.UserService
import zio.http.{Status => StatusResponse}
import zio.http.Header._
import zio.http.RequestHandlerMiddleware.WithOut
import zio.http._
import zio.http.codec.{ContentCodec, Doc, HeaderCodec, HttpCodec, HttpCodecType, PathCodec, TextCodec}
import zio.http.codec.HttpCodec._
import zio.http.codec.HttpCodecType.ResponseType
import zio.http.endpoint.EndpointMiddleware._
import zio.http.endpoint._
import zio.http.endpoint.openapi.OpenAPI.SecurityScheme.ApiKey.In
import zio.{ZIO, ZNothing, http}

import scala.reflect.runtime.universe.Typed

object Handlers {
	import ApiError._
	
	val signUp: Routes[AuthService, ApiError, None] =
		Endpoint
		.post(path = "api" / "v1" / "user" / "sign-up")
		.inCodec(ContentCodec.content[SignUpRequest](MediaType.application.json))
		.out[Unit]
		.outErrors[ApiError](
			HttpCodec.error[PhoneAlreadyUsed](StatusResponse.Conflict),
			HttpCodec.error[PasswordsNotEqual](StatusResponse.Conflict)
		)
		.implement{ req =>
			Password.getSalt.flatMap { salt =>
				(Password.cryptoSafe(req.password)(salt) zipPar Password.cryptoSafe(req.passwordAgain)(salt)).flatMap { case (pass_1, pass_2) =>
					if (pass_1 != pass_2) {
						ZIO.fail(PasswordsNotEqual()).unit
					} else {
						ZIO.serviceWithZIO[AuthService](_.signUp(req.copy(password =  pass_1), salt)).mapBoth(_ => PhoneAlreadyUsed(), _ => ZIO.unit)
					}
				}
			}
		}
	
	
	private val signInEndpointMiddleware =
		EndpointMiddleware(input = HttpCodec.authorization, output = HttpCodec.authorization, error = HttpCodec.empty)
			.optional
	
	private def signInIncomingMiddleware(input: Option[Authorization]): ZIO[AuthService, Unit, Option[Authorization]] = {
		ZIO.serviceWithZIO[AuthService](_.verify(input)).orElseFail(ZIO.unit)
	}
	private def signInOutgoingMiddleware(output: Option[Authorization]) : ZIO[Any, ZNothing, Option[Authorization]] = {
	 ZIO.succeed(output)
	}
	
	private val routes: RoutesMiddleware[AuthService, Option[Authorization], EndpointMiddleware.Typed[Option[Authorization], Unit, Option[Authorization]]] =
		RoutesMiddleware.make(signInEndpointMiddleware)(signInIncomingMiddleware)(signInOutgoingMiddleware)
	
	val signIn = Endpoint
		.post(path = "api" / "v1" / "user" / "sign-in")
		.in[SignInRequest]
		.out[Unit]
		.@@(signInEndpointMiddleware)
		.outErrors[ApiError](
			HttpCodec.error[PasswordsOrPhoneIncorrect](StatusResponse.Conflict) ,
			HttpCodec.error[PasswordsNotEqual](StatusResponse.Conflict)
		)
	
	val signInHandler: Routes[AuthService, ApiError, EndpointMiddleware.Typed[Option[Authorization], Unit, Option[Authorization]]] =
		signIn.implement { signInRequest =>
				ZIO.serviceWithZIO[AuthService](_.signIn(signInRequest))
					.mapBoth(_ => PasswordsOrPhoneIncorrect(), data => data)
		}
//		.implement { req =>
//			ZIO.serviceWithZIO[AuthService](_.signIn(req)).mapBoth( _ => PasswordsOrPhoneIncorrect(), data =>  data)
//		}

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
val authApi = signUp.toApp ++ signInHandler.toApp(routes)// ++ AuthMiddleware.secure(signOut.toApp)
}

