package com.ecom.point.share

import enumeratum._
import io.getquill.MappedEncoding
import zio.json._
import zio.prelude.Assertion.matches
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
		implicit val ord: Ord[Type] = implicitly[Ord[A]].contramap(unwrap)
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
		def unwrap[FROM, TO, _ <: RichNewtype[FROM]#Type](a: TO)(implicit equiv: Equivalence[FROM, TO]): FROM = { equiv.from(a)}
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
	
	sealed trait BankType extends EnumEntry
	
	object BankType extends Enum[BankType] {
		override val values: IndexedSeq[BankType] = findValues
		
		case object TOCHKA extends BankType
		
		case object SBERBANK extends BankType
		
		case object TINKOFF extends BankType
		
		implicit val bankCodec: JsonCodec[BankType] = JsonCodec[BankType](
			JsonEncoder[String].contramap[BankType](_.entryName),
			JsonDecoder[String].mapOrFail(name => BankType.withNameEither(name).left.map(_.getMessage)),
		)
	}
	
	sealed trait StatementDirection extends EnumEntry
	
	object StatementDirection extends Enum[StatementDirection] {
		override val values: IndexedSeq[StatementDirection] = findValues
		
		case object INCOME extends StatementDirection
		
		case object OUTCOME extends StatementDirection
		
		implicit val directionCodec: JsonCodec[StatementDirection] = JsonCodec[StatementDirection](
			JsonEncoder[String].contramap[StatementDirection](_.entryName),
			JsonDecoder[String].mapOrFail(name => StatementDirection.withNameEither(name).left.map(_.getMessage)),
		)
	}
	
	object AccountId extends StringType
	
	type AccountId = AccountId.Type
	
	
	object TaxId extends RichSubtype[String] {
		override def assertion: QuotedAssertion[String] = assert {
			matches("""^(\d{10}|\d{12})$""".r)
		}
	}
	
	type TaxId = TaxId.Type
	
	object CompanyName extends StringType
	type CompanyName = CompanyName.Type
	
	object StatementId extends StringType
	type StatementId = StatementId.Type
	
	object UserId extends UUIDType
	type UserId = UserId.Type
	
	object AccessTokenId extends StringType
	type AccessTokenId = AccessTokenId.Type
	
	object AccessToken extends StringType
	type AccessToken = AccessToken.Type
	
	object RefreshToken extends StringType
	type RefreshToken = RefreshToken.Type
	
	object ExpirationTokenDate extends InstantType
	type ExpirationTokenDate = ExpirationTokenDate.Type
	
	object PhoneNumber extends StringType
	type PhoneNumber = PhoneNumber.Type
	
	object Password extends StringType
	type Password = Password.Type
	
	object Name extends StringType
	type Name = Name.Type
	
	object ActivateDate extends InstantType
	type ActivateDate = ActivateDate.Type
	
	object BlockDate extends InstantType
	type BlockDate = BlockDate.Type
	
	object CreatedDate extends InstantType
	type CreatedDate = CreatedDate.Type
	
	object LastLoginDate extends InstantType
	type LastLoginDate = LastLoginDate.Type
	
	
	
	implicit def newtypeEncoder[A, T <: RichNewtype[A]#Type](implicit equiv: Equivalence[A, T]): MappedEncoding[T, A] = MappedEncoding[T, A](RichNewtype.unwrap(_))
	implicit def newtypeDecoder[A, T <: RichNewtype[A]#Type](implicit equiv: Equivalence[A, T]): MappedEncoding[A, T] = MappedEncoding[A, T](RichNewtype.wrap(_))
}
