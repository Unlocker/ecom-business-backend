package com.ecom.point.banks

import zio.json.{JsonDecoder, JsonEncoder}
import zio.prelude.{Equal, Newtype, Ord}

import java.time.Instant
import java.util.UUID

object Entities {
	implicit val ordInstant : Ord[Instant] = Ord.fromScala[Instant]
	
	object AccessTokenId extends Newtype[UUID] {
		implicit val eq: Equal[AccessTokenId.Type] = Equal.default
		implicit val jsonEncoder: JsonEncoder[AccessTokenId.Type] = JsonEncoder[UUID].contramap(AccessTokenId.unwrap)
		implicit val jsonDecoder: JsonDecoder[AccessTokenId.Type] = JsonDecoder[UUID].map(AccessTokenId.wrap)
	}
	
	object AccessToken extends Newtype[String] {
		implicit val eq: Equal[AccessToken.Type] = Equal.default
		implicit val jsonEncoder: JsonEncoder[AccessToken.Type] = JsonEncoder[String].contramap(AccessToken.unwrap)
		implicit val jsonDecoder: JsonDecoder[AccessToken.Type] = JsonDecoder[String].map(AccessToken.wrap)
	}
	
	object RefreshToken extends Newtype[String] {
		implicit val eq: Equal[RefreshToken.Type] = Equal.default
		implicit val jsonEncoder: JsonEncoder[RefreshToken.Type] = JsonEncoder[String].contramap(RefreshToken.unwrap)
		implicit val jsonDecoder: JsonDecoder[RefreshToken.Type] = JsonDecoder[String].map(RefreshToken.wrap)
	}
	
	object CancelAccessTokenDate extends Newtype[Instant] {
		implicit val ord: Ord[CancelAccessTokenDate.Type] = Ord[Instant].contramap { i => CancelAccessTokenDate.unwrap(i) }
		implicit val jsonEncoder: JsonEncoder[CancelAccessTokenDate.Type] = JsonEncoder[Instant].contramap(CancelAccessTokenDate.unwrap)
		implicit val jsonDecoder: JsonDecoder[CancelAccessTokenDate.Type] = JsonDecoder[Instant].map(CancelAccessTokenDate.wrap)
	}
}
