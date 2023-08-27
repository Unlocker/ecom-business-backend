//package com.ecom.point.share
//
//import com.ecom.point.share.entities.AccessToken
//import com.ecom.point.share.services.AuthService
//import zio.ZIO
//import zio.http.{RequestHandlerMiddleware, RequestHandlerMiddlewares}
//
//object Middllewares {
//	val auth: RequestHandlerMiddleware[Nothing, AuthService, Boolean, Any] = RequestHandlerMiddlewares.bearerAuthZIO(token =>
//		for {
//			service 	<- ZIO.service[AuthService]
//			access		<- service.auth(AccessToken(token)).mapBoth(_ => false, _ => true)
//		} yield access
//	)
//}
