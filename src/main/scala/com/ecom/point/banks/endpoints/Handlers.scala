package com.ecom.point.banks.endpoints


import com.ecom.point.banks.endpoints.EndpointData.TochkaBankAuthorizeResponse
import com.ecom.point.banks.services.TochkaBankService
import com.ecom.point.users.services.UserService
import sttp.client3.httpclient.zio.SttpClient
import zio.{ZIO, ZNothing, http}
import zio.http.endpoint._
import zio.http.endpoint.EndpointMiddleware.None
import zio.http.codec.HttpCodec._
import zio.http.MediaType
import zio.http.codec.ContentCodec

//object Handlers {
//
//	def authorize: Routes[TochkaBankService with SttpClient, ZNothing, None] = {
//		Endpoint
//			.post(literal("authorize"))
//			.outCodec(ContentCodec.content[TochkaBankAuthorizeResponse](MediaType.application.json))
//			.implement{
//				for {
//					user <- ZIO.serviceWithZIO[TochkaBankService](_.authorize())
//				} yield()
//			}
//	}
//
//	val clientBankApi: ZIO[TochkaBankService with UserService with SttpClient, Throwable, Unit] =  {}
//}
