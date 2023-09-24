package com.ecom.point.users.endpoints

import com.ecom.point.share.services.AuthService
import com.ecom.point.share.types.Password
import com.ecom.point.users.endpoints.EndpointData._
import zio.http.Header._
import zio.http.Method.GET
import zio.http.codec.HttpCodec._
import zio.http.codec.{ContentCodec, HttpCodec, PathCodec}
import zio.http.endpoint.EndpointMiddleware._
import zio.http.endpoint._
import zio.http.{Status => StatusResponse, _}
import zio.{ZIO, ZNothing}

object Handlers {
	import ApiError._
	
	val api: Path = Root / "api" / "v1" / "user"
	val apiEndpoint: PathCodec[Unit] =  "api" / "v1" / "user"
	
	val signUp: Routes[AuthService, ApiError, None] =
		Endpoint
		.post(path = apiEndpoint / "sign-up")
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
		.post(path = apiEndpoint / "sign-in")
		.in[SignInRequest]
		.out[SignInResponse]
		.outErrors[ApiError](
			HttpCodec.error[PasswordsOrPhoneIncorrect](StatusResponse.Conflict) ,
			HttpCodec.error[PasswordsNotEqual](StatusResponse.Conflict)
		)
	
	private val signInHandler = {
		signIn.implement { signInRequest =>
			ZIO.serviceWithZIO[AuthService](_.signIn(signInRequest))
				.mapBoth(_ => PasswordsOrPhoneIncorrect(), data => SignInResponse(data.accessToken, data.expirationTokenDate))
		}
	}
	
		val app = Http.collectZIO[Request]{
			case zio.http.Method.GET -> api / "sign-off" => ZIO.succeed(Response.ok.removeHeader("Authorization"))
		}
	
	
	//	private val signOff = Endpoint
	//		.get(path = "api" / "v1" / "user" / "sign-off")
	//		.@@(signOutEndpointMiddleware)
	//		.implement{ resp =>
	//			Handler.fromFunctionZIO{ _ =>
	//				ZIO.unit
	//			}
	//		}
	
	
	val authApi = signUp.toApp ++ signInHandler.toApp ++ app // ++ signOff.toApp(routes)// ++ AuthMiddleware.secure(signOut.toApp)
}

