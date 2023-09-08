package com.ecom.point.banks.services

import com.ecom.point.banks.models.{BankAccessToken, BankAccountBalance, BankStatement}
import com.ecom.point.banks.repos.BankRepository
import com.ecom.point.configs.TochkaBankConfig
import com.ecom.point.share.types.{AccessToken, AccessTokenId, AccountId, ExpirationTokenDate, RefreshToken}
import com.ecom.point.users.models.User
import com.ecom.point.utils.{AppError, InternalError}
import sttp.client3.SttpBackend
import sttp.model.Uri
import zio.json.{DeriveJsonCodec, JsonCodec}
import zio.{IO, Task, ZIO, ZLayer}

import java.net.URI
import java.time.{Instant, LocalDate}
import java.util.UUID

trait TochkaBankService {
  def authorize(user: User): IO[AppError, Option[URI]]

  def fetchToken(user: User, code: String): IO[AppError, BankAccessToken]

  def refreshToken(user: User, token: BankAccessToken): IO[AppError, BankAccessToken]

  def balances(user: User, token: BankAccessToken): IO[AppError, List[BankAccountBalance]]

  def statements(user: User, token: BankAccessToken, accountId: AccountId, start: LocalDate, end: LocalDate): IO[AppError, List[BankStatement]]
}

object TochkaBankService {
  def layer: ZLayer[SttpBackend[Task, Any] with TochkaBankConfig with BankRepository, Nothing, TochkaBankServiceLive] = ZLayer.fromFunction(TochkaBankServiceLive.apply _)
}

final case class TochkaBankServiceLive(
                                        client: SttpBackend[Task, Any],
                                        config: TochkaBankConfig,
                                        bankRepository: BankRepository
                                      )
  extends TochkaBankService {

  import sttp.client3._
  import sttp.client3.ziojson._

  private final case class BearerAccessToken(token_type: String, refresh_token: String, access_token: String, expires_in: Int)

  implicit val bearerAccessTokenDecoder: JsonCodec[BearerAccessToken] = DeriveJsonCodec.gen[BearerAccessToken]

  override def authorize(user: User): IO[AppError, Option[URI]] =
    bankRepository.getBankAccessTokenByUserId(user.id)
      .map {
        case None => Some(URI.create(config.url))
        case Some(_) => None
      }
      .mapError((ex: Throwable) => InternalError(message = ex.getMessage))


  override def fetchToken(user: User, code: String): IO[AppError, BankAccessToken] = {
    val responseTask: Task[Response[Either[ResponseException[String, String], BearerAccessToken]]] = basicRequest
      .body(
        Map(
          "client_id" -> config.clientId,
          "client_secret" -> config.clientSecret,
          "grant_type" -> "authorization_code",
          "scope" -> "accounts balances statements",
          "code" -> code,
          "redirect_url" -> "http://localhost:8000/"
        )
      )
      .post(Uri.unsafeParse(config.url + "/connect/token"))
      .response(asJson[BearerAccessToken])
      .send(client)

    responseTask.flatMap {
        resp =>
          resp.body.fold(
            respEx => {
              ZIO.fail(InternalError(message = s"${respEx.getMessage}"))
            },
            t => bankRepository.createBankAccessToken(
              BankAccessToken(
                AccessTokenId(UUID.randomUUID()),
                AccessToken(t.access_token),
                RefreshToken(t.refresh_token),
                ExpirationTokenDate(Instant.now().plusSeconds(t.expires_in)),
                user.id
              )
            )
          )
      }
      .mapError((ex: Throwable) => InternalError(message = ex.getMessage))
  }

  override def refreshToken(user: User, token: BankAccessToken): IO[AppError, BankAccessToken] = ???

  override def balances(user: User, token: BankAccessToken): IO[AppError, List[BankAccountBalance]] = ???

  override def statements(user: User, token: BankAccessToken, accountId: AccountId, start: LocalDate, end: LocalDate): IO[AppError, List[BankStatement]] = ???
}
