package com.ecom.point.utils

import zio.json._
import zio.schema._
import zio.prelude._

package object types {
	abstract class RichNewtype[A : JsonCodec : Schema : Equal] extends Newtype[A] { self =>
		implicit val equiv: A <=> Type = Equivalence(wrap, unwrap)
		implicit val equal: Equal[Type] = implicitly[Equal[A]].contramap(unwrap)
		implicit val jsonCodec: JsonCodec[Type] = implicitly[JsonCodec[A]].transform(wrap, unwrap)
		implicit val schemeConverter: Schema[Type] = implicitly[Schema[A]].transform(wrap, unwrap)
		
		implicit final class UnwrapOps(value: Type) {
			def unwrap: A = self.unwrap(value)
		}
	}
	
	object RichNewtype {
		def wrap[FROM, TO <: RichNewtype[FROM]#Type](a: FROM)(implicit equiv: Equivalence[FROM, TO]): TO = equiv.to(a)
		def unwrap[FROM, TO, _ <: RichNewtype[FROM]#Type](a: TO)(implicit equiv: Equivalence[FROM, TO]): FROM = equiv.from(a)
	}
	
	abstract class RichSubtype[A: JsonCodec : Schema : Ord] extends Subtype[A] { self =>
		implicit val equiv: A <=> Type = Equivalence(wrap, unwrap)
		implicit val equal: Equal[Type] = implicitly[Equal[A]].contramap(unwrap)
		implicit val jsonCodec: JsonCodec[Type] = implicitly[JsonCodec[A]].transform(wrap, unwrap)
		implicit val schemeConverter: Schema[Type] = implicitly[Schema[A]].transform(wrap, unwrap)
		
		implicit final class UnwrapOps(value: Type) {
			def unwrap: A = self.unwrap(value)
		}
	}
	
	object RichSubtype {
		def wrap[FROM, TO <: RichSubtype[FROM]#Type](a: FROM)(implicit equiv: Equivalence[FROM, TO]): TO = equiv.to(a)
		def unwrap[FROM, TO, _ <: RichSubtype[FROM]#Type](a: TO)(implicit equiv: Equivalence[FROM, TO]): FROM = equiv.from(a)
	}
}
