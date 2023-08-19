package com.ecom.point
import com.ecom.point.configs.HttpConfig
import com.ecom.point.users.AccountRepository
import zio.http.Http
import zio.http.netty.*

final case class MainServer(httpConfig: HttpConfig,
														accountRepository: AccountService,
														bankRepository: BankService
													 ) {
	
	private def app = ???
	
}
