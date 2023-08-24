package com.ecom.point.share

import com.ecom.point.users.entities.Name
import zio.json.{JsonDecoder, JsonEncoder}
import zio.prelude.{Equal, Newtype, Ord}
import zio.schema.Schema

import java.time.Instant
import java.util.UUID

package object entities {
	implicit val ordInstant: Ord[Instant] = Ord.fromScala[Instant]
	
	type UserId = UserId.Type
	object UserId extends Newtype[UUID] {
		implicit val eq: Equal[UserId] = Equal.default
		implicit val jsonEncoder: JsonEncoder[UserId] = JsonEncoder[UUID].contramap(UserId.unwrap)
		implicit val jsonDecoder: JsonDecoder[UserId] = JsonDecoder[UUID].map(UserId.wrap)
		implicit val schema: Schema[UserId] = Schema.primitive[UUID].transform(str => UserId(str), tp => UserId.unwrap(tp))
	}
	
	type AccessTokenId = AccessTokenId.Type
	object AccessTokenId extends Newtype[UUID] {
		implicit val eq: Equal[AccessTokenId] = Equal.default
		implicit val jsonEncoder: JsonEncoder[AccessTokenId] = JsonEncoder[UUID].contramap(AccessTokenId.unwrap)
		implicit val jsonDecoder: JsonDecoder[AccessTokenId] = JsonDecoder[UUID].map(AccessTokenId.wrap)
		implicit val schema: Schema[AccessToken] = Schema.primitive[String].transform(str => AccessToken(str), tp => AccessToken.unwrap(tp))
	}
	
	type AccessToken = AccessToken.Type
	object AccessToken extends Newtype[String] {
		implicit val eq: Equal[AccessToken] = Equal.default
		implicit val jsonEncoder: JsonEncoder[AccessToken] = JsonEncoder[String].contramap(AccessToken.unwrap)
		implicit val jsonDecoder: JsonDecoder[AccessToken] = JsonDecoder[String].map(AccessToken.wrap)
		implicit val schema: Schema[AccessToken] = Schema.primitive[String].transform(str => AccessToken(str), tp => AccessToken.unwrap(tp))
	}
	
	type RefreshToken = RefreshToken.Type
	object RefreshToken extends Newtype[String] {
		implicit val eq: Equal[RefreshToken] = Equal.default
		implicit val jsonEncoder: JsonEncoder[RefreshToken] = JsonEncoder[String].contramap(RefreshToken.unwrap)
		implicit val jsonDecoder: JsonDecoder[RefreshToken] = JsonDecoder[String].map(RefreshToken.wrap)
		implicit val schema: Schema[RefreshToken] = Schema.primitive[String].transform(str => RefreshToken(str), tp => RefreshToken.unwrap(tp))
	}
	
	type ExpirationTokenDate = ExpirationTokenDate.Type
	object ExpirationTokenDate extends Newtype[Instant] {
		implicit val ord: Ord[ExpirationTokenDate] = Ord[Instant].contramap { i => ExpirationTokenDate.unwrap(i) }
		implicit val jsonEncoder: JsonEncoder[ExpirationTokenDate] = JsonEncoder[Instant].contramap(ExpirationTokenDate.unwrap)
		implicit val jsonDecoder: JsonDecoder[ExpirationTokenDate] = JsonDecoder[Instant].map(ExpirationTokenDate.wrap)
		implicit val schema: Schema[ExpirationTokenDate] = Schema.primitive[Instant].transform(str => ExpirationTokenDate(str), tp => ExpirationTokenDate.unwrap(tp))
	}
}
