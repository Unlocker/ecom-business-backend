package com.ecom.point.users.endpoints

import com.ecom.point.AuthMiddleware
import com.ecom.point.share.services.AuthService
import com.ecom.point.share.types.{AccessToken, Password, PhoneNumber}
import com.ecom.point.users.endpoints.EndpointData.{ApiError, PasswordsNotEqual, PasswordsOrPhoneIncorrect, PhoneAlreadyUsed, SignInRequest, SignInResponse, SignUpRequest}
import com.ecom.point.users.endpoints.Handlers.signOutIncomingMiddleware
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

import scala.reflect.runtime.universe.{Try, Typed}

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
	
	
	private val signOutEndpointMiddleware =
		EndpointMiddleware(input = HttpCodec.authorization, output = HttpCodec.empty, error = HttpCodec.empty)
			.optionalIn
	
	private def signOutIncomingMiddleware(input: Option[Authorization]): ZIO[AuthService, Unit, Option[Authorization]] = {
		ZIO.serviceWithZIO[AuthService](_.verify(input)).orElseFail(ZIO.unit)
	}
	private def signInOutgoingMiddleware(output: Option[Authorization]) : ZIO[Any, ZNothing, Unit] = {
	 ZIO.unit
	}
	
	private val routes: RoutesMiddleware[AuthService, Option[Authorization], EndpointMiddleware.Typed[Option[Authorization], Unit, Unit]] =
		RoutesMiddleware.make(signOutEndpointMiddleware)(signOutIncomingMiddleware)(signInOutgoingMiddleware)
	
	private val signIn = Endpoint
		.post(path = "api" / "v1" / "user" / "sign-in")
		.in[SignInRequest]
		.out[SignInResponse]
		.outErrors[ApiError](
			HttpCodec.error[PasswordsOrPhoneIncorrect](StatusResponse.Conflict) ,
			HttpCodec.error[PasswordsNotEqual](StatusResponse.Conflict)
		)
	
	private val signInHandler =
		signIn.implement { signInRequest =>
				ZIO.serviceWithZIO[AuthService](_.signIn(signInRequest))
					.mapBoth(_ => PasswordsOrPhoneIncorrect(), data => SignInResponse(data.accessToken, data.expirationTokenDate))
		}
	


	private val signOff = Endpoint
		.get(path = "api" / "v1" / "user" / "sign-off")
		.@@(signOutEndpointMiddleware)
		.implement{ resp =>
			Handler.fromFunctionZIO{ _ =>
				ZIO.unit
			}
		}
		
	
	val authApi = signUp.toApp ++ signInHandler.toApp ++ signOff.toApp(routes)// ++ AuthMiddleware.secure(signOut.toApp)
}

