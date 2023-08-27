package com.ecom.point

import com.ecom.point.configs.QuillContext
import com.ecom.point.users.endpoints.Handlers
import com.ecom.point.users.repos.UserRepository
import com.ecom.point.users.services.UserService
import zio.http._
import zio.{Scope, ZIO, ZIOAppArgs, ZIOAppDefault}

object Main extends ZIOAppDefault {
	
	override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] = {
		
		Server
			.serve(Handlers.authApi)
			.provide(
				Server.default,
				UserService.layer,
				UserRepository.layer,
				QuillContext.layer
			)
	}
	
	
	
	//		private val server = ZIO.serviceWithZIO[MainServer](_.start)
	//
	//		private val nThreads: Int = 8
	//
	//		private val runningServer: URIO[Any, ExitCode] =
	//			server
	//				.provide(
	//					MainServer.layer,
	//					UserServiceLive.layer,
	//					AuthService.layer,
	//					UserRepositoryLive.layer,
	//					QuillContext.layer
	//				)
	//
	//		override def run: ZIO[Any, IOException, ExitCode] = Console.println("Server initialize") *> runningServer
	
}
