package com.ecom.point.banks.endpoints

import com.ecom.point.banks.models.BankAccountBalance
import zio.schema.{DeriveSchema, Schema}

import java.net.URI

object EndpointData {

  final case class TochkaBankAuthorizeResponse(tokenReceived: Boolean, redirectUrl: Option[URI])

  final case class TochkaAcceptOauthQuery(code: String, state: String)

  implicit val tochkaBankAuthorizeResponseSchema: Schema[TochkaBankAuthorizeResponse] = DeriveSchema.gen[TochkaBankAuthorizeResponse]
  implicit val tochkaAcceptOauthQuerySchema: Schema[TochkaAcceptOauthQuery] = DeriveSchema.gen[TochkaAcceptOauthQuery]
  implicit val bankAccountBalanceSchema: Schema[BankAccountBalance] = DeriveSchema.gen[BankAccountBalance]
}
