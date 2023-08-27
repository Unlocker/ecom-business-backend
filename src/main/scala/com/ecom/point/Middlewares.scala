//package com.ecom.point
//
//import com.ecom.point.share.entities.AccessToken
//import com.ecom.point.share.services.AuthService
//import zio.ZIO
//import zio.http.RequestHandlerMiddlewares
//
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
