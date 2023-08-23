package com.ecom.point

import com.ecom.point.configs.{HttpConfig, QuillContext}
import com.ecom.point.share.services.{AuthService, AuthServiceImpl}
import com.ecom.point.users.repos.{UserRepository, UserRepositoryLive}
import com.ecom.point.users.services.UserServiceLive
import io.getquill.jdbczio.Quill.DataSource
import zio.http.Server
import zio.http.endpoint.openapi.OpenAPI.SecurityScheme.Http
import zio.{ExitCode, URIO, ZIO, ZIOAppDefault}

import scala.sys.process.processInternal.IOException


class Main extends ZIOAppDefault{
	
		private val server = ZIO.serviceWithZIO[MainServer](_.start)
		
		private val nThreads: Int = 8
		
		private val runningServer: URIO[Any, ExitCode] =
			server
				.provide(
					MainServer.layer,
					UserServiceLive.layer,
					AuthService.layer,
					UserRepositoryLive.layer,
					QuillContext.layer
				)
		
		override def run: ZIO[Any, IOException, ExitCode] = Console.println("Server initialize") *> runningServer
	
}
