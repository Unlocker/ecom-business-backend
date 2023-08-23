package com.ecom.point
import com.ecom.point.configs.HttpConfig
import com.ecom.point.share.services.AuthService
import com.ecom.point.users.endpoints.Handlers
import com.ecom.point.users.repos.{UserRepository, UserRepositoryLive}
import com.ecom.point.users.services.{UserService, UserServiceLive}
import zio.{&, ZEnvironment, ZIO, ZLayer, http}
import zio.http.{Http, Server}

import javax.sql.DataSource


final case class MainServer(
													 	httpConfig: HttpConfig,
														userService: UserService,
														authService: AuthService
													 ) {
	
	private def app = {
		val authApi: http.App[AuthService & UserService] = Handlers.authApi
		authApi
	}
	
	val start =
		Server
		.serve(app)
			.provide(Server.default) *>

}

object MainServer {
	val layer: ZLayer[HttpConfig & UserService & AuthService, Nothing, MainServer] = {
		ZLayer.fromFunction(MainServer.apply _)
	}
}
