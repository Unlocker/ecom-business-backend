package com.ecom.point.banks

import com.ecom.point.utils.types._
import enumeratum._
import zio.json.{JsonCodec, JsonDecoder, JsonEncoder}
import zio.prelude.Assertion._
import zio.prelude.QuotedAssertion
package object entities {
  
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
}
