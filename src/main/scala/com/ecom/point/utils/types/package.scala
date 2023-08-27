package com.ecom.point.utils

import zio.json._
import zio.schema._
import zio.prelude._

import java.time.Instant
import java.util.UUID

package object types {
	implicit val ordInstant: Ord[Instant] = Ord.fromScala[Instant]
	implicit val eqUUID: Equal[UUID] = Equal.fromScala[UUID]
	implicit val ordUUID: Ord[UUID] = Ord.fromScala[UUID]
	
	abstract class StringType extends RichNewtype[String]
	abstract class UUIDType extends RichNewtype[UUID]
	abstract class InstantType extends RichNewtype[Instant]
	
	abstract class RichNewtype[A : JsonCodec : Schema : Equal : Ord] extends Newtype[A] { self =>
		implicit val equiv: A <=> Type = Equivalence(wrap, unwrap)
		implicit val equal: Equal[Type] = implicitly[Equal[A]].contramap(unwrap)
		implicit val ord: Equal[Type] = implicitly[Ord[A]].contramap(unwrap)
		implicit val jsonCodec: JsonCodec[Type] = implicitly[JsonCodec[A]].transform(wrap, unwrap)
		implicit val schemeConverter: Schema[Type] = implicitly[Schema[A]].transform(wrap, unwrap)
		
		implicit final class UnwrapOps(value: Type) {
			def unwrap: A = self.unwrap(value)
		}
		
		implicit final class WrapOps(value: A) {
			def unwrap: Type = self.wrap(value)
		}
	}
	
	object RichNewtype {
		def wrap[FROM, TO <: RichNewtype[FROM]#Type](a: FROM)(implicit equiv: Equivalence[FROM, TO]): TO = equiv.to(a)
		def unwrap[FROM, TO, _ <: RichNewtype[FROM]#Type](a: TO)(implicit equiv: Equivalence[FROM, TO]): FROM = {println("SSSSSSSSSSSSSSSS"); equiv.from(a)}
	}
	
	abstract class RichSubtype[A: JsonCodec : Schema : Ord] extends Subtype[A] { self =>
		implicit val equiv: A <=> Type = Equivalence(wrap, unwrap)
		implicit val equal: Equal[Type] = implicitly[Equal[A]].contramap(unwrap)
		implicit val jsonCodec: JsonCodec[Type] = implicitly[JsonCodec[A]].transform(wrap, unwrap)
		implicit val schemeConverter: Schema[Type] = implicitly[Schema[A]].transform(wrap, unwrap)
		
		implicit final class UnwrapOps(value: Type) {
			def unwrap: A = self.unwrap(value)
		}
		
		implicit final class WrapOps(value: A) {
			def unwrap: Type = self.wrap(value)
		}
	}
	
	object RichSubtype {
		def wrap[FROM, TO <: RichSubtype[FROM]#Type](a: FROM)(implicit equiv: Equivalence[FROM, TO]): TO = equiv.to(a)
		def unwrap[FROM, TO, _ <: RichSubtype[FROM]#Type](a: TO)(implicit equiv: Equivalence[FROM, TO]): FROM = equiv.from(a)
	}
}
