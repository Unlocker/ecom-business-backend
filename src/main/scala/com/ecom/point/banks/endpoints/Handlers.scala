package com.ecom.point.banks.endpoints


import com.ecom.point.banks.endpoints.EndpointData._
import com.ecom.point.banks.models.BankAccountBalance
import com.ecom.point.banks.services.TochkaBankService
import com.ecom.point.share.services.AuthService
import com.ecom.point.share.types.AccountId
import com.ecom.point.users.models.User
import zio.http.Header.Authorization
import zio.http.{Handler, MediaType, Response}
import zio.http.codec.HttpCodec._
import zio.http.codec.{ContentCodec, HttpCodec, PathCodec}
import zio.http.endpoint.EndpointMiddleware._
import zio.http.endpoint._
import zio.{ZIO, ZNothing}
import zio.http.Header._

object Handlers {
  
  private val endpointMiddleware =
    EndpointMiddleware(input = HttpCodec.authorization, output = HttpCodec.authorization , error = HttpCodec.empty)
      .optional
  
  private def in(input: Option[Authorization]): ZIO[AuthService, ZNothing, Option[(User, Authorization)]] = {
    ZIO.serviceWithZIO[AuthService](_.verifyAndReturnUser(input)).orDie
  }
  
  private def out(output: Option[(User,Authorization)]): ZIO[Any, ZNothing, Option[Authorization]] = {
    ZIO.succeed(output.map(_._2))
  }
  
  private val routes: RoutesMiddleware[AuthService, Option[(User,Authorization)], EndpointMiddleware.Typed[Option[Authorization], Unit, Option[Authorization]]] =
    RoutesMiddleware.make(endpointMiddleware)(in)(out)
    
  
  private val pathPrefix: PathCodec[Unit] = "api" / "v1" / "bank" / "TOCHKA"
  
  val authorize: Routes[TochkaBankService with AuthService, ZNothing, Typed[Option[Authorization], Unit, Option[Authorization]]] = Endpoint
    .get(path = pathPrefix / "authorize")
    .out[TochkaBankAuthorizeResponse]
    .@@(endpointMiddleware)
    .header(authorization)
    .implement { req =>
      val res = for{
        user <- ZIO.serviceWithZIO[AuthService](_.getUserByAuthToken(req)).orDie
        maybeUri <- ZIO.serviceWithZIO[TochkaBankService]{_.authorize(user.get)}.orDie
      } yield TochkaBankAuthorizeResponse(maybeUri.isEmpty, maybeUri)
      res
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
//  	def authorize: Routes[TochkaBankService with SttpClient, ZNothing, None] = {
//  		Endpoint
//  			.post(literal("authorize"))
//  			.outCodec(ContentCodec.content[TochkaBankAuthorizeResponse](MediaType.application.json))
//  			.implement{
//  				for {
//  					user <- ZIO.serviceWithZIO[TochkaBankService](_.authorize())
//  				} yield()
//  			}
//  	}
  //
  //	val clientBankApi: ZIO[TochkaBankService with UserService with SttpClient, Throwable, Unit] =  {}
}
