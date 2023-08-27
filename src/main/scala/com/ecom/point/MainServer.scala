package com.ecom.point
import com.ecom.point.configs.HttpConfig
import com.ecom.point.share.services.AuthService
import com.ecom.point.users.endpoints.Handlers
import com.ecom.point.users.repos.{UserRepository, UserRepositoryLive}
import com.ecom.point.users.services.{UserService,  UserServiceLive}
import zio.{&, ZEnvironment, ZIO, ZLayer, http}
import zio.http.{Http, Server}

import javax.sql.DataSource
import scala.language.postfixOps


//final case class MainServer(
//													 	httpConfig: HttpConfig,
//														userService: UserService,
//														authService: AuthService
//													 ) {
//
//	private def app = {
//		val authApi: http.App[UserServiceFake] = Handlers.authApi
//		authApi
//	}
//
////	val start =
////		Server
////		.serve(app)
////			.provide(Server.defaultWith(_.binding(httpConfig.host, httpConfig.port)) ++ Ma) *>
//
//}
//
//object MainServer {
//	val layer: ZLayer[HttpConfig with UserService with AuthService, Nothing, MainServer] = {
//		ZLayer.fromFunction(MainServer.apply _)
//	}
//}
