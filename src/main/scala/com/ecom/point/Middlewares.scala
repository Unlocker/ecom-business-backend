package com.ecom.point


import com.ecom.point.share.services.AuthService
import com.ecom.point.share.types.AccessToken
import com.ecom.point.users.models.User
import zio.{ZIO, _}
import zio.http._



object AuthMiddleware{
	
	def secure[R](http: App[R & (AccessToken, User)]): App[R & AuthService] = {
		http @@ auth[R]
	}
	
	private def auth[R0] = RequestHandlerMiddlewares.customAuthProvidingZIO[R0, AuthService, Nothing, (AccessToken, User)] { data =>
		data.headers.get(Header.Authorization) match {
			case Some(Header.Authorization.Bearer(token)) =>
				ZIO.serviceWithZIO[AuthService] { service => service.auth(AccessToken(token)) }
					.fold(_ => None, x => Option(x))
			case _ => ZIO.succeed(None)
		}
	}
}

