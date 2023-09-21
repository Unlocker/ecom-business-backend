package com.ecom.point.banks.endpoints


import com.ecom.point.banks.endpoints.EndpointData._
import com.ecom.point.banks.models.BankAccountBalance
import com.ecom.point.banks.services.TochkaBankService
import com.ecom.point.share.types.AccountId
import com.ecom.point.users.models.User
import zio.http.MediaType
import zio.http.codec.HttpCodec._
import zio.http.codec.{ContentCodec, PathCodec}
import zio.http.endpoint.EndpointMiddleware._
import zio.http.endpoint._
import zio.{ZIO, ZNothing}

object Handlers {

  private val pathPrefix: PathCodec[Unit] = "api" / "v1" / "bank" / "TOCHKA"
  val authorize: Routes[TochkaBankService, ZNothing, None] = Endpoint
    .get(path = pathPrefix / "authorize")
    .out[TochkaBankAuthorizeResponse]
    .outCodec(ContentCodec.content[TochkaBankAuthorizeResponse](MediaType.application.json))
    .implement { req =>
      val user: User = ???
      ZIO.serviceWithZIO[TochkaBankService] { bankService =>
        bankService.authorize(user).map(maybeUri => TochkaBankAuthorizeResponse(maybeUri.isEmpty, maybeUri)).orDie
      }
    }

  val acceptOauth = Endpoint
    .get(path = pathPrefix / "accept-oauth" ^? paramStr("code") & paramStr("state"))
    .implement { req: (String, String) =>
      val user: User = ??? // get by state (userId)
      val code: String = req._1 // get for query parameter
      ZIO.serviceWithZIO[TochkaBankService] { service =>
        service.fetchToken(user, code)
        ???
      }
    }

  val balances = Endpoint
    .get(pathPrefix / "balance")
    .outCodec(ContentCodec.content[List[BankAccountBalance]](MediaType.application.json))
    .implement { req =>
      val user: User = ???

      /**
       * val res = for {
       * repo: BankRepository <- ZIO.serviceWithZIO[BankRepository]
       * service: TochkaBankService <- ZIO.serviceWithZIO[TochkaBankService]
       * } yield {
       * repo.getBankAccessTokenByUserId(user.id).flatMap{
       * case scala.None => ZIO.fail(InternalError(message = "empty token"))
       * case Some(token) => service.balances(user, token)
       * }
       * }
       * */
      ???
    }

  val transactions = Endpoint
    .get(
      pathPrefix / "balance" / PathCodec.string("accountId") / "transactions" / PathCodec.string("start")
        / PathCodec.string("end")
    )
    .implement {
      case (accId, start, end) =>
        val user: User = ???
        val accountId = AccountId(accId)
        ???
    }


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
}
