package com.ecom.point.banks.endpoints

import com.ecom.point.banks.models.{BankAccountBalance, BankTransaction}
import zio.schema._

import java.net.URI
import java.util.Currency

object EndpointData {

  final case class TochkaBankAuthorizeResponse(tokenReceived: Boolean, redirectUrl: Option[URI])

  final case class TochkaAcceptOauthQuery(code: String, state: String)

  implicit val tochkaBankAuthorizeResponseSchema: Schema[TochkaBankAuthorizeResponse] = DeriveSchema.gen[TochkaBankAuthorizeResponse]
  implicit val tochkaAcceptOauthQuerySchema: Schema[TochkaAcceptOauthQuery] = DeriveSchema.gen[TochkaAcceptOauthQuery]
  implicit val currencySchema: Schema[Currency] = Schema.primitive[String].transform[Currency](Currency.getInstance, _.getCurrencyCode)
  implicit val bankAccountBalanceSchema: Schema[BankAccountBalance] = DeriveSchema.gen[BankAccountBalance]
  implicit val bankTransactionSchema: Schema[BankTransaction] = DeriveSchema.gen[BankTransaction]
}
