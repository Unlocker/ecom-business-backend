package com.ecom.point.users

import zio.json.{JsonDecoder, JsonEncoder}
import zio.prelude.{Equal, Newtype, Ord}
import zio.schema.{DeriveSchema, Deriver, Schema}

import java.time.Instant

package object entities {
	implicit val ordInstant: Ord[Instant] = Ord.fromScala[Instant]
	
	type PhoneNumber = PhoneNumber.Type
	object PhoneNumber extends Newtype[String]{
		implicit val eq: Equal[PhoneNumber] = Equal.default
		implicit val jsonEncoder: JsonEncoder[PhoneNumber] = JsonEncoder[String].contramap(PhoneNumber.unwrap)
		implicit val jsonDecoder: JsonDecoder[PhoneNumber] = JsonDecoder[String].map(PhoneNumber.wrap)
		implicit val schema: Schema[PhoneNumber] = Schema.primitive[String].transform(str => PhoneNumber(str), tp => PhoneNumber.unwrap(tp))
	}
	
	type Password  = Password.Type
	object Password extends Newtype[String] {
		implicit val eq: Equal[Password] = Equal.default
		implicit val jsonEncoder: JsonEncoder[Password] = JsonEncoder[String].contramap(Password.unwrap)
		implicit val jsonDecoder: JsonDecoder[Password] = JsonDecoder[String].map(Password.wrap)
		implicit val schema: Schema[Password] = Schema.primitive[String].transform(str => Password(str), tp => Password.unwrap(tp))
	}
	
	
	type Name = Name.Type
	object Name extends Newtype[String] {
		implicit val eq: Equal[Name] = Equal.default
		implicit val jsonEncoder: JsonEncoder[Name] = JsonEncoder[String].contramap(Name.unwrap)
		implicit val jsonDecoder: JsonDecoder[Name] = JsonDecoder[String].map(Name.wrap)
		implicit val schema: Schema[Name] = Schema.primitive[String].transform(str => Name(str), tp => Name.unwrap(tp))
	}
	
	type ActivateDate = ActivateDate.Type
	object ActivateDate extends Newtype[Instant] {
		implicit val ord: Ord[ActivateDate] = Ord[Instant].contramap { i => ActivateDate.unwrap(i) }
		implicit val jsonEncoder: JsonEncoder[ActivateDate] = JsonEncoder[Instant].contramap(ActivateDate.unwrap)
		implicit val jsonDecoder: JsonDecoder[ActivateDate] = JsonDecoder[Instant].map(ActivateDate.wrap)
		implicit val schema: Schema[ActivateDate] = Schema.primitive[Instant].transform(str => ActivateDate(str), tp => ActivateDate.unwrap(tp))
	}
	
	type BlockDate = BlockDate.Type
	object BlockDate extends Newtype[Instant] {
		implicit val ord: Ord[BlockDate] = Ord[Instant].contramap { i => BlockDate.unwrap(i) }
		implicit val jsonEncoder: JsonEncoder[BlockDate] = JsonEncoder[Instant].contramap(BlockDate.unwrap)
		implicit val jsonDecoder: JsonDecoder[BlockDate] = JsonDecoder[Instant].map(BlockDate.wrap)
		implicit val schema: Schema[BlockDate] = Schema.primitive[Instant].transform(str => BlockDate(str), tp => BlockDate.unwrap(tp))
	}
	
	type CreatedDate = CreatedDate.Type
	object CreatedDate extends Newtype[Instant] {
		implicit val ord: Ord[CreatedDate.Type] = Ord[Instant].contramap { i => CreatedDate.unwrap(i) }
		implicit val jsonEncoder: JsonEncoder[CreatedDate.Type] = JsonEncoder[Instant].contramap(CreatedDate.unwrap)
		implicit val jsonDecoder: JsonDecoder[CreatedDate.Type] = JsonDecoder[Instant].map(CreatedDate.wrap)
		implicit val schema: Schema[CreatedDate] = Schema.primitive[Instant].transform(str => CreatedDate(str), tp => CreatedDate.unwrap(tp))
	}
	
	type LastLoginDate = LastLoginDate.Type
	object LastLoginDate extends Newtype[Instant] {
		implicit val ord: Ord[LastLoginDate] = Ord[Instant].contramap { i => LastLoginDate.unwrap(i) }
		implicit val jsonEncoder: JsonEncoder[LastLoginDate] = JsonEncoder[Instant].contramap(LastLoginDate.unwrap)
		implicit val jsonDecoder: JsonDecoder[LastLoginDate] = JsonDecoder[Instant].map(LastLoginDate.wrap)
		implicit val schema: Schema[LastLoginDate] = Schema.primitive[Instant].transform(str => LastLoginDate(str), tp => LastLoginDate.unwrap(tp))
	}
}
