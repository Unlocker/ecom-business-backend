package com.ecom.point.share

import com.ecom.point.share
import zio.json.JsonCodec
import zio.prelude.{Equal, Ord}
import zio.schema.Schema
import com.ecom.point.utils.types._
import zio.prelude

import java.time.Instant
import java.util.UUID

package object entities {
	implicit val ordInstant: Ord[Instant] = Ord.fromScala[Instant]
	implicit val equalUUID: Equal[UUID] = Equal.fromScala[UUID]
	type UserId = UserId.Type
	object UserId extends RichNewtype[UUID]
	
	type AccessTokenId = AccessTokenId.Type
	object AccessTokenId extends RichNewtype[UUID]
	
	type AccessToken = AccessToken.Type
	object AccessToken extends RichNewtype[String]
	
	type RefreshToken = RefreshToken.Type
	object RefreshToken extends RichNewtype[String]
	
	type ExpirationTokenDate = ExpirationTokenDate.Type
	object ExpirationTokenDate extends RichNewtype[Instant] {
		implicit val ord: Ord[ExpirationTokenDate] = Ord[Instant].contramap(unwrap)
	}
}
