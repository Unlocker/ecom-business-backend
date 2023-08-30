package com.ecom.point.banks.endpoints

import zio.schema.{DeriveSchema, Schema}

import java.net.URI

object EndpointData {

  final case class TochkaBankAuthorizeResponse(tokenReceived: Boolean, redirectUrl: Option[URI])

  object TochkaBankAuthorizeResponse {
    implicit val tochkaBankAuthorizeResponseSchema: Schema[TochkaBankAuthorizeResponse] = DeriveSchema.gen[TochkaBankAuthorizeResponse]
  }
}
