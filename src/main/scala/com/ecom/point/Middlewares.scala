package com.ecom.point


import com.ecom.point.share.services.AuthService
import com.ecom.point.share.types.{AccessToken, Password, PhoneNumber}
import com.ecom.point.users.endpoints.EndpointData.{ApiError, PasswordsOrPhoneIncorrect, SignInRequest}
import com.ecom.point.users.models.User
import zio.{ZIO, _}
import zio.http._



object AuthMiddleware{
	
	def secure[R](http: App[R & (AccessToken, User)]): App[R & AuthService] = {
		http @@  auth[R]
	}
	
	private def auth[R0] = RequestHandlerMiddlewares.customAuthProvidingZIO[R0, AuthService, Nothing, (AccessToken, User)] { data =>
		data.headers.get(Header.Authorization) match {
			case Some(Header.Authorization.Bearer(token)) =>
				ZIO.serviceWithZIO[AuthService] { service => service.auth(AccessToken(token)) }
					.fold(_ => None, x => Option(x))
			case _ => ZIO.succeed(None)
		}
	}
	
	def bearerAuth: RequestHandlerMiddleware.Simple[AuthService, Any] =  new RequestHandlerMiddleware.Simple[AuthService, Any] {
		override def apply[Env <: AuthService, Err >: Any](handler: Handler[Env, Err, Request, Response])(implicit trace: Trace): Handler[Env, Err, Request, Response] = {
			Handler.fromFunctionZIO[Request]{ request =>
				request.headers.get(Header.Authorization) match {
					case Some(Header.Authorization.Bearer(token)) =>
						ZIO.serviceWithZIO[AuthService] { service =>
							service.auth(AccessToken(token))
								.mapBoth(
									_ => handler.runZIO(request),
									token => handler.runZIO(request).map(res => res.addHeader(Header.Authorization.Bearer(token._1.unwrap)))
							)
						}.flatten
					case _ => handler.runZIO(request)
				}
			}
		}
	}
	
	def bearerAuthZIO = new RequestHandlerMiddleware.Simple[AuthService, Any] {
		override def apply[Env <: AuthService, Err >: Any](handler: Handler[Env, Err, Request, Response])(implicit trace: Trace): Handler[Env, Err, Request, Response] = {
			Handler.fromFunctionZIO[Request] {request =>
				request.headers.get(Header.Authorization) match {
					case Some(Header.Authorization.Bearer(token)) =>
						ZIO.serviceWithZIO[AuthService] { service =>
							service.auth(AccessToken(token))
								.mapBoth(
									_ => ZIO.serviceWithZIO[AuthService](_.signIn(SignInRequest(PhoneNumber(""), Password(""))))
										.mapBoth( _ => handler.runZIO(request), data =>  handler.runZIO(request).map(res => res.addHeader(Header.Authorization.Bearer(data.get.unwrap)))),
									token => handler.runZIO(request).map(res => res.addHeader(Header.Authorization.Bearer(token._1.unwrap)))
								)
						}.flatten
					case _ => handler.runZIO(request)
				}
			}
		}
	}
}

