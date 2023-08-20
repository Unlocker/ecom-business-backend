package com.ecom.point.share

import zio.json.{JsonDecoder, JsonEncoder}
import zio.prelude.{Equal, Newtype, Ord}

import java.time.Instant
import java.util.UUID

package object entities {
	implicit val ordInstant: Ord[Instant] = Ord.fromScala[Instant]
	
	object UserId extends Newtype[UUID] {
		implicit val eq: Equal[UserId.Type] = Equal.default
		implicit val jsonEncoder: JsonEncoder[UserId.Type] = JsonEncoder[UUID].contramap(UserId.unwrap)
		implicit val jsonDecoder: JsonDecoder[UserId.Type] = JsonDecoder[UUID].map(UserId.wrap)
	}
	
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
	
	object ExpirationTokenDate extends Newtype[Instant] {
		implicit val ord: Ord[ExpirationTokenDate.Type] = Ord[Instant].contramap { i => ExpirationTokenDate.unwrap(i) }
		implicit val jsonEncoder: JsonEncoder[ExpirationTokenDate.Type] = JsonEncoder[Instant].contramap(ExpirationTokenDate.unwrap)
		implicit val jsonDecoder: JsonDecoder[ExpirationTokenDate.Type] = JsonDecoder[Instant].map(ExpirationTokenDate.wrap)
	}
}
