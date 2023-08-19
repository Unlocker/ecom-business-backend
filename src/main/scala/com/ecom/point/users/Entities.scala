package com.ecom.point.users

import java.time._
import zio.json.{JsonDecoder, JsonEncoder}
import zio.prelude.{Equal, Newtype, Ord}

import java.util.UUID

object Entities {
	implicit val ordInstant: Ord[Instant] = Ord.fromScala[Instant]
	
	object AccountId extends Newtype[UUID]{
		implicit val eq: Equal[AccountId.Type] = Equal.default
		implicit val jsonEncoder: JsonEncoder[AccountId.Type] = JsonEncoder[UUID].contramap(AccountId.unwrap)
		implicit val jsonDecoder: JsonDecoder[AccountId.Type] = JsonDecoder[UUID].map(AccountId.wrap)
	}
	
	object PhoneNumber extends Newtype[String]{
		implicit val eq: Equal[PhoneNumber.Type] = Equal.default
		implicit val jsonEncoder: JsonEncoder[PhoneNumber.Type] = JsonEncoder[String].contramap(PhoneNumber.unwrap)
		implicit val jsonDecoder: JsonDecoder[PhoneNumber.Type] = JsonDecoder[String].map(PhoneNumber.wrap)
	}
	
	object Password extends Newtype[String] {
		implicit val eq: Equal[Password.Type] = Equal.default
		implicit val jsonEncoder: JsonEncoder[Password.Type] = JsonEncoder[String].contramap(Password.unwrap)
		implicit val jsonDecoder: JsonDecoder[Password.Type] = JsonDecoder[String].map(Password.wrap)
	}
	
	object Name extends Newtype[String] {
		implicit val eq: Equal[Name.Type] = Equal.default
		implicit val jsonEncoder: JsonEncoder[Name.Type] = JsonEncoder[String].contramap(Name.unwrap)
		implicit val jsonDecoder: JsonDecoder[Name.Type] = JsonDecoder[String].map(Name.wrap)
	}
	
	object ActivateDate extends Newtype[Instant] {
		implicit val ord: Ord[ActivateDate.Type] = Ord[Instant].contramap { i => ActivateDate.unwrap(i) }
		implicit val jsonEncoder: JsonEncoder[ActivateDate.Type] = JsonEncoder[Instant].contramap(ActivateDate.unwrap)
		implicit val jsonDecoder: JsonDecoder[ActivateDate.Type] = JsonDecoder[Instant].map(ActivateDate.wrap)
	}
	
	object BlockDate extends Newtype[Instant] {
		implicit val ord: Ord[BlockDate.Type] = Ord[Instant].contramap { i => BlockDate.unwrap(i) }
		implicit val jsonEncoder: JsonEncoder[BlockDate.Type] = JsonEncoder[Instant].contramap(BlockDate.unwrap)
		implicit val jsonDecoder: JsonDecoder[BlockDate.Type] = JsonDecoder[Instant].map(BlockDate.wrap)
	}
	
	object CreatedDate extends Newtype[Instant] {
		implicit val ord: Ord[CreatedDate.Type] = Ord[Instant].contramap { i => CreatedDate.unwrap(i) }
		implicit val jsonEncoder: JsonEncoder[CreatedDate.Type] = JsonEncoder[Instant].contramap(CreatedDate.unwrap)
		implicit val jsonDecoder: JsonDecoder[CreatedDate.Type] = JsonDecoder[Instant].map(CreatedDate.wrap)
	}
	
	object LastLoginDate extends Newtype[Instant] {
		implicit val ord: Ord[LastLoginDate.Type] = Ord[Instant].contramap { i => LastLoginDate.unwrap(i) }
		implicit val jsonEncoder: JsonEncoder[LastLoginDate.Type] = JsonEncoder[Instant].contramap(LastLoginDate.unwrap)
		implicit val jsonDecoder: JsonDecoder[LastLoginDate.Type] = JsonDecoder[Instant].map(LastLoginDate.wrap)
	}
}
