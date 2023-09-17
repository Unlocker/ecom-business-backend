package com.ecom.point.banks.services

import com.ecom.point.banks.models._
import com.ecom.point.banks.repos.BankRepository
import com.ecom.point.configs.TochkaBankConfig
import com.ecom.point.share.types._
import com.ecom.point.users.models.User
import com.ecom.point.utils.{AppError, InternalError}
import sttp.client3.SttpBackend
import sttp.client3.httpclient.zio.SttpClient
import sttp.model.MediaType.{ApplicationJson, ApplicationXWwwFormUrlencoded}
import zio.http.Charsets
import zio.json.{DeriveJsonCodec, JsonCodec}
import zio.{IO, Task, ZIO, ZLayer}

import java.net.{URI, URLEncoder}
import java.time.{Instant, LocalDate, ZonedDateTime}
import java.util.UUID

trait TochkaBankService {
  def authorize(user: User): IO[AppError, Option[URI]]

  def fetchToken(user: User, code: String): IO[AppError, BankAccessToken]

  def refreshToken(user: User, token: BankAccessToken): IO[AppError, BankAccessToken]

  def balances(user: User, token: BankAccessToken): IO[AppError, List[BankAccountBalance]]

  def transactions(
                    user: User,
                    token: BankAccessToken,
                    accountId: AccountId,
                    start: LocalDate,
                    end: LocalDate
                  ): IO[AppError, List[BankTransaction]]
}

object TochkaBankService {
  def layer: ZLayer[SttpClient with TochkaBankConfig with BankRepository, Nothing, TochkaBankServiceLive]
  = ZLayer.fromFunction(TochkaBankServiceLive.apply _)
}

final case class TochkaBankServiceLive(
                                        client: SttpBackend[Task, Any],
                                        config: TochkaBankConfig,
                                        bankRepository: BankRepository
                                      )
  extends TochkaBankService {

  import sttp.client3._
  import sttp.client3.ziojson._

  final case class TochkaAccessToken(token_type: String, refresh_token: String, access_token: String, expires_in: Int)

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

  private val tokenUri = uri"${config.url}/connect/token"

  override def authorize(user: User): IO[AppError, Option[URI]] =
    bankRepository.getBankAccessTokenByUserId(user.id)
      .flatMap {
        case None => makeRedirectUrl(user).map(Some(_))
        case Some(_) => ZIO.succeed(None)
      }
      .mapError(errorHandler)

  private val scope = "accounts balances statements"

  private case class TochkaConsent(consentId: String)

  private case class TochkaConsentResponse(Data: TochkaConsent)

  private implicit val tochkaConsentDecoder: JsonCodec[TochkaConsent] = DeriveJsonCodec.gen[TochkaConsent]
  private implicit val tochkaConsentResponseDecoder: JsonCodec[TochkaConsentResponse] = DeriveJsonCodec.gen[TochkaConsentResponse]


  def makeRedirectUrl(user: User): IO[AppError, URI] = {
    val uriEffect = for {
      serviceToken <- basicRequest
        .contentType(ApplicationXWwwFormUrlencoded)
        .body(
          Map(
            "client_id" -> config.clientId,
            "client_secret" -> config.clientSecret,
            "grant_type" -> "client_credentials",
            "scope" -> scope
          )
        )
        .post(tokenUri)
        .response(asJsonAlways[TochkaAccessToken])
        .send(client)
        .flatMap(_.body.fold(ex => ZIO.fail(InternalError(message = ex.getMessage)), t => ZIO.succeed(t.access_token)))

      consentId <- basicRequest
        .contentType(ApplicationJson)
        .auth.bearer(serviceToken)
        .body(
          """
            |{
            |  "Data" : {
            |    "permissions": [
            |      "ReadAccountsBasic", "ReadAccountsDetail", "ReadBalances", "ReadStatements"
            |    ]
            |  }
            |} """.stripMargin
        )
        .post(uri"${config.url}/uapi/v1.0/consents")
        .response(asJsonAlways[TochkaConsentResponse])
        .send(client)
        .flatMap(_.body.fold(ex => ZIO.fail(InternalError(message = ex.getMessage)), r => ZIO.succeed(r.Data.consentId)))
    } yield {
      URI.create(
        s"${config.url}/connect/authorize" +
          s"?client_id=${config.clientId}" +
          s"&response_type=code%20id_token" +
          s"&state=${user.id.unwrap}" +
          s"&redirect_uri=${URLEncoder.encode(config.redirectUri, Charsets.Utf8)}" +
          s"&scope=${URLEncoder.encode(scope, Charsets.Utf8)}" +
          s"&consent_id=$consentId"
      )
    }
    uriEffect.mapError(errorHandler)
  }

  override def fetchToken(user: User, code: String): IO[AppError, BankAccessToken] = {
    val responseTask = basicRequest
      .body(
        Map(
          "client_id" -> config.clientId,
          "client_secret" -> config.clientSecret,
          "grant_type" -> "authorization_code",
          "scope" -> scope,
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

  private implicit val tochkaBalanceDecoder: JsonCodec[TochkaBalance] = DeriveJsonCodec.gen[TochkaBalance]
  private implicit val tochkaBalanceListDecoder: JsonCodec[TochkaBalanceList] = DeriveJsonCodec.gen[TochkaBalanceList]
  private implicit val tochkaBalanceResponseDecoder: JsonCodec[TochkaBalanceResponse] = DeriveJsonCodec.gen[TochkaBalanceResponse]

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

  private implicit val TochkaStatementParamsDecoder: JsonCodec[TochkaStatementParams] = DeriveJsonCodec.gen[TochkaStatementParams]
  private implicit val tochkaStatementQueryDecoder: JsonCodec[TochkaStatementQuery] = DeriveJsonCodec.gen[TochkaStatementQuery]
  private implicit val TochkaStatementRequestDecoder: JsonCodec[TochkaStatementRequest] = DeriveJsonCodec.gen[TochkaStatementRequest]

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

  private implicit val tochkaPartyDecoder: JsonCodec[TochkaParty] = DeriveJsonCodec.gen[TochkaParty]
  private implicit val tochkaTransactionDecoder: JsonCodec[TochkaTransaction] = DeriveJsonCodec.gen[TochkaTransaction]
  private implicit val tochkaStatementDecoder: JsonCodec[TochkaStatement] = DeriveJsonCodec.gen[TochkaStatement]
  private implicit val tochkaStatementResultDecoder: JsonCodec[TochkaStatementResult] = DeriveJsonCodec.gen[TochkaStatementResult]
  private implicit val tochkaStatementResponseDecoder: JsonCodec[TochkaStatementResponse] = DeriveJsonCodec.gen[TochkaStatementResponse]

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

                  val partyConverter: TochkaParty => IO[AppError, Counterparty] = p =>
                    TaxId.make(p.inn).map(Counterparty(CompanyName(p.name), _))
                      .toZIO
                      .mapError(mes => InternalError(message = s"Incorrect party tax id is '$mes''"))

                  for {
                    creditor <- partyConverter(tran.CreditorParty)
                    debtor <- partyConverter(tran.DebtorParty)
                  } yield {
                    BankTransaction(
                      TransactionId(tran.transactionId),
                      AccountId(stmt.accountId),
                      BankType.TOCHKA,
                      direction,
                      tran.documentProcessDate,
                      direction match {
                        case StatementDirection.INCOME => creditor
                        case StatementDirection.OUTCOME => debtor
                      },
                      tran.Amount
                    )
                  }
              }
              ZIO.collectAll(transactions)
            }
          )
      }
      .mapError(errorHandler)
  }
}
