package com.ecom.point

import com.ecom.point.banks.endpoints.{Handlers => BankHndlers}
import com.ecom.point.configs.{QuillContext, TochkaBankConfig}
import com.ecom.point.share.services.AuthService
import com.ecom.point.users.endpoints.{Handlers => ServerHandlers}
import zio.ZLayer
import zio.config.typesafe.TypesafeConfigProvider
//import com.ecom.point.banks.endpoints.{Handlers => ClientHandlers}
import com.ecom.point.banks.repos.BankRepository
import com.ecom.point.banks.services.TochkaBankService
import com.ecom.point.users.repos.UserRepository
import com.ecom.point.users.services.UserService
import sttp.client3.httpclient.zio.HttpClientZioBackend
import zio.http._
import zio.{Scope, ZIO, ZIOAppArgs, ZIOAppDefault}

object Main extends ZIOAppDefault {
	
	override val bootstrap: ZLayer[ZIOAppArgs, Any, Any] = {
		zio.Runtime.setConfigProvider(
			TypesafeConfigProvider.fromResourcePath()
		)
	}
	
	override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] = {
	
		
		Server
			.serve((ServerHandlers.authApi ++ BankHndlers.bankApi)  @@ HttpAppMiddleware.requestLogging())
			.provide(
				Server.default,
				UserService.layer,
				TochkaBankConfig.layer,
				TochkaBankService.layer,
				UserRepository.layer,
				BankRepository.layer,
				QuillContext.layer,
				AuthService.layer,
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
