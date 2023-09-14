package com.ecom.point

import com.ecom.point.configs.{QuillContext, TochkaBankConfig}
import com.ecom.point.users.endpoints.{Handlers => ServerHandlers}
import com.ecom.point.banks.endpoints.{Handlers => ClientHandlers}
import com.ecom.point.banks.repos.BankRepository
import com.ecom.point.banks.services.TochkaBankService
import com.ecom.point.users.repos.UserRepository
import com.ecom.point.users.services.UserService
import io.getquill.SnakeCase
import io.getquill.jdbczio.Quill
import sttp.client3.httpclient.zio.HttpClientZioBackend
import sttp.client3.armeria.ArmeriaWebClient
import sttp.client3.testing
import sttp.client3.testing.SttpBackendStub
import zio.http._
import zio.{Scope, ZIO, ZIOAppArgs, ZIOAppDefault}

object Main extends ZIOAppDefault {
	
	override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] = {
	
		
		Server
			.serve(ServerHandlers.authApi @@ AuthMiddleware.authorization _)
			.provide(
				Server.default,
				Client.default,
				UserService.layer,
				TochkaBankService.layer,
				UserRepository.layer,
				BankRepository.layer,
				QuillContext.layer,
				TochkaBankConfig.layer,
				HttpClientZioBackend.layer()
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
