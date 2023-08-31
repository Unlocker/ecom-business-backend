package com.ecom.point.banks.endpoints


import com.ecom.point.banks.services.TochkaBankService
import zio.{ZIO, http}
import zio.http.Client
import zio.http.Cookie.Request

object Handlers {
	val url = "http://sports.api.decathlon.com/groups/water-aerobics"
	def authorize = {
		for{
			res <- Client.request(url)
			h <- ZIO.serviceWithZIO[TochkaBankService](_.authorize())
		} yield ()
	}
	val clientBankApi: ZIO[TochkaBankService with Client, Throwable, Unit] = authorize
}
