package com.ecom.point.users

import com.ecom.point.utils.types._
import zio.json.JsonCodec
import zio.prelude.{Equal, Ord}
import zio.schema.Schema

import java.time.Instant

package object entities {
	implicit val ordInstant: Ord[Instant] = Ord.fromScala[Instant]
	
	type PhoneNumber = PhoneNumber.Type
	object PhoneNumber extends RichNewtype[String]
	
	type Password  = Password.Type
	object Password extends RichNewtype[String]
	
	type Name = Name.Type
	object Name extends RichNewtype[String]
	
	type ActivateDate = ActivateDate.Type
	object ActivateDate extends RichNewtype[Instant]
	
	type BlockDate = BlockDate.Type
	
	object BlockDate extends RichNewtype[Instant] {
		implicit val ord: Ord[BlockDate] = Ord[Instant].contramap {unwrap}
	}
	
	type CreatedDate = CreatedDate.Type
	object CreatedDate extends RichNewtype[Instant] {
		implicit val ord: Ord[CreatedDate] = Ord[Instant].contramap {unwrap}
	}
	
	type LastLoginDate = LastLoginDate.Type
	object LastLoginDate extends RichNewtype[Instant] {
		implicit val ord: Ord[LastLoginDate] = Ord[Instant].contramap {unwrap}
	}
}
