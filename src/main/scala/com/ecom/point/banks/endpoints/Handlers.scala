package com.ecom.point.banks.endpoints


import com.ecom.point.banks.endpoints.EndpointData._
import com.ecom.point.banks.models.{BankAccountBalance, BankTransaction}
import com.ecom.point.banks.services.TochkaBankService
import com.ecom.point.share.services.AuthService
import com.ecom.point.share.types.{AccountId, UserId}
import com.ecom.point.users.models.User
import com.ecom.point.users.services.UserService
import com.ecom.point.utils.InternalError
import zio.http.Header.Authorization
import zio.http.codec.HttpCodec._
import zio.http.codec.{HttpCodec, PathCodec}
import zio.http.endpoint._
import zio.{ZIO, ZNothing, http}

import java.time.LocalDate
import java.util.UUID

object Handlers {

  private val endpointMiddleware =
    EndpointMiddleware(input = HttpCodec.authorization, output = HttpCodec.authorization, error = HttpCodec.empty)
      .optional

  private def in(input: Option[Authorization]): ZIO[AuthService, ZNothing, Option[(User, Authorization)]] = {
    ZIO.serviceWithZIO[AuthService](_.verifyAndReturnUser(input)).orDie
  }

  private def out(output: Option[(User, Authorization)]): ZIO[Any, ZNothing, Option[Authorization]] = {
    ZIO.succeed(output.map(_._2))
  }

  private val routes: RoutesMiddleware[AuthService, Option[(User, Authorization)], EndpointMiddleware.Typed[Option[Authorization], Unit, Option[Authorization]]] =
    RoutesMiddleware.make(endpointMiddleware)(in)(out)


  private val pathPrefix: PathCodec[Unit] = "api" / "v1" / "bank" / "TOCHKA"

  val authorize: http.App[TochkaBankService with AuthService] = Endpoint
    .get(path = pathPrefix / "authorize")
    .out[TochkaBankAuthorizeResponse]
    .@@(endpointMiddleware)
    .header(authorization)
    .implement { req =>
      val res = for {
        user <- ZIO.serviceWithZIO[AuthService](_.getUserByAuthToken(req)).orDie
        maybeUri <- ZIO.serviceWithZIO[TochkaBankService](_.authorize(user.get)).orDie
      } yield TochkaBankAuthorizeResponse(maybeUri.isEmpty, maybeUri)
      res
    }.toApp(routes)

  val acceptOauth: http.App[TochkaBankService with UserService] = Endpoint
    .get(path = pathPrefix / "accept-oauth" ^? paramStr("code") & paramStr("state"))
    .out[TochkaBankAuthorizeResponse]
    .implement { case (code: String, state: String) =>
      val res = for {
        user <- ZIO.serviceWithZIO[UserService](_.getUserById(UserId(UUID.fromString(state)))).orDie
        _ <- ZIO.serviceWithZIO[TochkaBankService](_.fetchToken(user.get, code)).orDie
      } yield TochkaBankAuthorizeResponse(tokenReceived = true, Option.empty)
      res
    }.toApp

  val balances: http.App[TochkaBankService with AuthService] = Endpoint
    .get(pathPrefix / "balance")
    .out[List[BankAccountBalance]]
    .@@(endpointMiddleware)
    .header(authorization)
    .implement { req: Authorization =>
      for {
        maybeUser <- ZIO.serviceWithZIO[AuthService](_.getUserByAuthToken(req)).orDie
        balances <- ZIO.serviceWithZIO[TochkaBankService] { bankService =>
          val user = maybeUser.get
          bankService.tokenByUser(user).flatMap {
            case Some(token) => bankService.balances(user, token)
            case _ => ZIO.fail(InternalError(message = s"No token for USER=$user"))
          }
        }.orDie
      } yield balances
    }.toApp(routes)

  val transactions: http.App[TochkaBankService with AuthService] = Endpoint
    .get(
      pathPrefix / "balance" / PathCodec.string("accountId") / "transactions" / PathCodec.string("start")
        / PathCodec.string("end")
    )
    .out[List[BankTransaction]]
    .@@(endpointMiddleware)
    .header(authorization)
    .implement {
      case (accId, start, end, auth) => for {
        maybeUser <- ZIO.serviceWithZIO[AuthService](_.getUserByAuthToken(auth)).orDie
        transactions <- ZIO.serviceWithZIO[TochkaBankService] { bankService =>
          val user = maybeUser.get
          bankService.tokenByUser(user).flatMap {
            case Some(token) => bankService.transactions(user, token, AccountId(accId), LocalDate.parse(start), LocalDate.parse(end))
            case _ => ZIO.fail(InternalError(message = s"No token for USER=$user"))
          }.orDie
        }
      } yield {
        transactions
      }
    }.toApp(routes)


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
