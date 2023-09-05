package com.ecom.point.banks.endpoints


import com.ecom.point.banks.services.TochkaBankService
import zio.{ZIO, http}
import zio.http.Client
import zio.http.Cookie.Request

object Handlers {
	
	def authorize = {
	
	}
	val clientBankApi: ZIO[TochkaBankService with Client, Throwable, Unit] =  ???
}
