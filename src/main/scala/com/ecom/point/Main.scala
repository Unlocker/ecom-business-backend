package com.ecom.point

import com.ecom.point.configs.QuillContext
import com.ecom.point.users.endpoints.{Handlers => ServerHandlers}
import com.ecom.point.banks.endpoints.{Handlers => ClientHandlers}
import com.ecom.point.banks.repos.BankRepository
import com.ecom.point.banks.services.TochkaBankService
import com.ecom.point.users.repos.UserRepository
import com.ecom.point.users.services.UserService
import io.getquill.SnakeCase
import io.getquill.jdbczio.Quill
import zio.http._
import zio.{Scope, ZIO, ZIOAppArgs, ZIOAppDefault}

object Main extends ZIOAppDefault {
	
	override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] = {
		val client = ClientHandlers
			.clientBankApi.
			provide(
				Client.default,
				TochkaBankService.layer,
				BankRepository.layer,
				QuillContext.layer
			)
		
		val server = Server
			.serve(ServerHandlers.authApi @@ RequestHandlerMiddlewares.requestLogging())
			.provide(
				Server.default,
				UserService.layer,
				UserRepository.layer,
				QuillContext.layer
			)
		server zip client
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
