package com.ecom.point


import com.ecom.point.share.repos.TokenDbo
import com.ecom.point.share.services.AuthService
import com.ecom.point.share.types.AccessToken
import com.ecom.point.users.models.User
import sttp.client3.HttpError
import zio.{Trace, ZIO, http}
import zio.http.HttpError.{BadRequest, Unauthorized}
import zio.http.{Handler, Header, Headers, Http, HttpAppMiddleware, Request, RequestHandlerMiddleware, RequestHandlerMiddlewares, Response, Status}

//object Middlewares {
//	def authentification = RequestHandlerMiddlewares.bearerAuthZIO { jwtToken =>
//		for {
//			service <- ZIO.service[AuthService]
//			resp <- service.auth(AccessToken(jwtToken)).mapBoth(_ => false, _ => true)
//		} yield resp
//	}
//
//	def authorization = RequestHandlerMiddlewares.bearerAuthZIO { jwtToken =>
//		for {
//			service <- ZIO.service[AuthService]
//			resp <- service.auth(AccessToken(jwtToken)).mapBoth(_ => true, _ => false)
//		} yield resp
//	}
//
//	def logger = RequestHandlerMiddlewares.requestLogging()
//}

object AuthMiddleware{
	private val isExistType: Headers => Boolean = headers =>  headers.exists(h => h.headerType == Header.Authorization)
	
	def authorization = RequestHandlerMiddlewares.whenHeader[AuthService, Unauthorized](isExistType){
		new RequestHandlerMiddleware.Simple[AuthService, Unauthorized] {
			override def apply[Env <: AuthService, Err >: Unauthorized](handler: Handler[Env, Err, Request, Response])(implicit trace: Trace): Handler[Env, Err, Request, Response] = {
				handler
			}
		}
	}
	
	def verifyToken = RequestHandlerMiddlewares.ifHeaderThenElse(isExistType)(
		ifFalse = Handler.status(Status.Unauthorized).addHeaders(Headers.empty),
		ifTrue = new RequestHandlerMiddleware.Simple[AuthService, Nothing]{
			override def apply[Env <: AuthService, Err >: Nothing](handler: Handler[Env, Err, Request, Response])(implicit trace: Trace): Handler[Env, Err, Request, Response] = {
				Handler.fromFunctionHandler[Request]{ request =>
					val currentToken = RequestHandlerMiddlewares.bearerAuth()
					ZIO.serviceWithZIO[AuthService](_.auth(request.))
				}
			}
		}
	)
}

