package com.ecom.point

import zio.http.{Request, RequestHandlerMiddleware, RequestHandlerMiddlewares}

object Middlewares {
	def auth = RequestHandlerMiddlewares.bearerAuthZIO{	jwtToken =>
		
	}
	
	def logger = RequestHandlerMiddlewares.requestLogging()
}
