package com.ecom.point.banks.services

import com.ecom.point.banks.models._
import com.ecom.point.banks.repos.BankRepository
import com.ecom.point.configs.TochkaBankConfig
import com.ecom.point.share.types._
import com.ecom.point.users.models.User
import com.ecom.point.utils.{AppError, InternalError}
import sttp.client3.SttpBackend
import zio.json.{DeriveJsonCodec, JsonCodec}
import zio.{IO, Task, ZIO, ZLayer}

import java.net.URI
import java.time.{Instant, LocalDate, ZonedDateTime}
import java.util.UUID

trait TochkaBankService {
  def authorize(user: User): IO[AppError, Option[URI]]

  def fetchToken(user: User, code: String): IO[AppError, BankAccessToken]

  def refreshToken(user: User, token: BankAccessToken): IO[AppError, BankAccessToken]

  def balances(user: User, token: BankAccessToken): IO[AppError, List[BankAccountBalance]]

  def transactions(user: User, token: BankAccessToken, accountId: AccountId, start: LocalDate, end: LocalDate): IO[AppError, List[BankTransaction]]
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

  private final case class TochkaAccessToken(token_type: String, refresh_token: String, access_token: String, expires_in: Int)

  implicit val tochkaAccessTokenDecoder: JsonCodec[TochkaAccessToken] = DeriveJsonCodec.gen[TochkaAccessToken]

  private val errorHandler: PartialFunction[Throwable, AppError] = {
    case x: AppError => x
    case ex => InternalError(message = ex.getMessage)
  }

  private val newTokenFunc: UserId => TochkaAccessToken => BankAccessToken = userId => bearer => BankAccessToken(
    AccessTokenId(UUID.randomUUID()),
    AccessToken(bearer.access_token),
    RefreshToken(bearer.refresh_token),
    ExpirationTokenDate(Instant.now().plusSeconds(bearer.expires_in)),
    userId
  )

  override def authorize(user: User): IO[AppError, Option[URI]] =
    bankRepository.getBankAccessTokenByUserId(user.id)
      .map {
        case None => Some(URI.create(config.url))
        case Some(_) => None
      }
      .mapError(errorHandler)


  private val tokenUri = uri"${config.url}/connect/token"

  override def fetchToken(user: User, code: String): IO[AppError, BankAccessToken] = {
    val responseTask = basicRequest
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
      .post(tokenUri)
      .response(asJson[TochkaAccessToken])
      .send(client)

    responseTask.flatMap {
        resp =>
          resp.body.fold(
            respEx => {
              ZIO.fail(InternalError(message = s"${respEx.getMessage}"))
            },
            t => bankRepository.createBankAccessToken(newTokenFunc(user.id)(t))
          )
      }
      .mapError(errorHandler)
  }

  override def refreshToken(user: User, token: BankAccessToken): IO[AppError, BankAccessToken] = {
    val responseTask = basicRequest
      .body(
        Map(
          "client_id" -> config.clientId,
          "client_secret" -> config.clientSecret,
          "grant_type" -> "refresh_token",
          "refresh_token" -> token.refreshToken.unwrap
        )
      )
      .post(tokenUri)
      .response(asJson[TochkaAccessToken])
      .send(client)

    responseTask.flatMap {
        resp =>
          resp.body.fold(
            respEx => {
              ZIO.fail(InternalError(message = s"${respEx.getMessage}"))
            },
            t => {
              val newToken = newTokenFunc(user.id)(t)
              bankRepository.deleteBankAccessToken(token.id)
                .flatMap(_ => bankRepository.createBankAccessToken(newToken))
            }
          )
      }
      .mapError(errorHandler)
  }

  private final case class TochkaBalance(
                                          accountId: String,
                                          dateTime: ZonedDateTime,
                                          Amount: Money
                                        )

  private final case class TochkaBalanceList(Balances: List[TochkaBalance])

  private final case class TochkaBalanceResponse(Data: TochkaBalanceList)

  implicit val tochkaBalanceDecoder: JsonCodec[TochkaBalance] = DeriveJsonCodec.gen[TochkaBalance]
  implicit val tochkaBalanceListDecoder: JsonCodec[TochkaBalanceList] = DeriveJsonCodec.gen[TochkaBalanceList]
  implicit val tochkaBalanceResponseDecoder: JsonCodec[TochkaBalanceResponse] = DeriveJsonCodec.gen[TochkaBalanceResponse]

  override def balances(user: User, token: BankAccessToken): IO[AppError, List[BankAccountBalance]] = {
    val balancesTask = basicRequest
      .auth.bearer(token.accessToken.unwrap)
      .get(uri"${config.url}/uapi/open-banking/v1.0/balances")
      .response(asJson[TochkaBalanceResponse])
      .send(client)

    balancesTask.flatMap {
        resp =>
          resp.body.fold(
            respEx => {
              ZIO.fail(InternalError(message = s"${respEx.getMessage}"))
            },
            balances => ZIO.succeed(
              balances.Data.Balances.map(b => BankAccountBalance(AccountId(b.accountId), BankType.TOCHKA, b.Amount))
            )
          )
      }
      .mapError(errorHandler)
  }

  private final case class TochkaStatementParams(accountId: String, startDateTime: LocalDate, endDateTime: LocalDate)

  private final case class TochkaStatementQuery(Statement: TochkaStatementParams)

  private final case class TochkaStatementRequest(Data: TochkaStatementQuery)

  implicit val tochkaStatementParamsDecoder: JsonCodec[TochkaStatementParams] = DeriveJsonCodec.gen[TochkaStatementParams]
  implicit val tochkaStatementQueryDecoder: JsonCodec[TochkaStatementQuery] = DeriveJsonCodec.gen[TochkaStatementQuery]
  implicit val tochkaStatementRequestDecoder: JsonCodec[TochkaStatementRequest] = DeriveJsonCodec.gen[TochkaStatementRequest]

  private final case class TochkaParty(inn: String, name: String)

  private final case class TochkaTransaction(
                                              transactionId: String,
                                              creditDebitIndicator: String,
                                              documentProcessDate: LocalDate,
                                              Amount: Money,
                                              DebtorParty: TochkaParty,
                                              CreditorParty: TochkaParty
                                            )

  private final case class TochkaStatement(
                                            accountId: String,
                                            statementId: String,
                                            startDateTime: LocalDate,
                                            endDateTime: LocalDate,
                                            Transaction: List[TochkaTransaction]
                                          )

  private final case class TochkaStatementResult(Statement: TochkaStatement)

  private final case class TochkaStatementResponse(Data: TochkaStatementResult)

  implicit val tochkaPartyDecoder: JsonCodec[TochkaParty] = DeriveJsonCodec.gen[TochkaParty]
  implicit val tochkaTransactionDecoder: JsonCodec[TochkaTransaction] = DeriveJsonCodec.gen[TochkaTransaction]
  implicit val tochkaStatementDecoder: JsonCodec[TochkaStatement] = DeriveJsonCodec.gen[TochkaStatement]
  implicit val tochkaStatementResultDecoder: JsonCodec[TochkaStatementResult] = DeriveJsonCodec.gen[TochkaStatementResult]
  implicit val tochkaStatementResponseDecoder: JsonCodec[TochkaStatementResponse] = DeriveJsonCodec.gen[TochkaStatementResponse]

  override def transactions(user: User, token: BankAccessToken, accountId: AccountId, start: LocalDate, end: LocalDate)
  : IO[AppError, List[BankTransaction]] = {
    val params = TochkaStatementParams(accountId = accountId.unwrap, startDateTime = start, endDateTime = end)
    val statementTask = basicRequest
      .auth.bearer(token.accessToken.unwrap)
      .post(uri"${config.url}/uapi/open-banking/v1.0/statements")
      .body(TochkaStatementRequest(TochkaStatementQuery(params)))
      .response(asJson[TochkaStatementResponse])
      .send(client)

    statementTask.flatMap {
        resp =>
          resp.body.fold(
            respEx => {
              ZIO.fail(InternalError(message = s"${respEx.getMessage}"))
            },
            data => {
              val stmt = data.Data.Statement
              val transactions = stmt.Transaction.map {
                tran =>
                  val direction = tran.creditDebitIndicator match {
                    case "Credit" => StatementDirection.INCOME
                    case "Debit" => StatementDirection.OUTCOME
                  }
                  BankTransaction(
                    TransactionId(tran.transactionId),
                    AccountId(stmt.accountId),
                    BankType.TOCHKA,
                    direction,
                    tran.documentProcessDate,
                    direction match {
                      case StatementDirection.INCOME => Counterparty(
                        CompanyName(tran.CreditorParty.name), TaxId(tran.CreditorParty.inn)
                      )
                      case StatementDirection.OUTCOME => Counterparty(
                        CompanyName(tran.DebtorParty.name), TaxId(tran.DebtorParty.inn)
                      )
                    },
                    tran.Amount
                  )
              }
              ZIO.succeed(transactions)
            }
          )
      }
      .mapError(errorHandler)
  }
}
