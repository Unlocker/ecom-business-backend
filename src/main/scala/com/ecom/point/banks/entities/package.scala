package com.ecom.point.banks

import zio.json.{JsonDecoder, JsonEncoder}
import zio.prelude.Assertion._
import zio.prelude.{Equal, Newtype, QuotedAssertion, Subtype}
import zio.schema.Schema

package object entities {
  object BankType extends Enumeration {
    type BankType = Value
    val TOCHKA, SBERBANK, TINKOFF = Value
  }

  object StatementDirection extends Enumeration {
    type StatementDirection = Value
    val INCOME = Value(1)
    val OUTCOME = Value(-1)
  }

  type AccountId = AccountId.Type

  object AccountId extends Newtype[String] {
    implicit val eq: Equal[AccountId] = Equal.default
    implicit val jsonEncoder: JsonEncoder[AccountId] = JsonEncoder[String].contramap(AccountId.unwrap)
    implicit val jsonDecoder: JsonDecoder[AccountId] = JsonDecoder[String].map(AccountId.wrap)
    implicit val schema: Schema[AccountId] = Schema.primitive[String].transform(str => AccountId(str), tp => AccountId.unwrap(tp))
  }

  type TaxId = TaxId.Type

  object TaxId extends Subtype[String] {
    override def assertion: QuotedAssertion[String] = assert {
      matches("""^(\d{10}|\d{12})$""".r)
    }

    implicit val eq: Equal[TaxId] = Equal.default
    implicit val jsonEncoder: JsonEncoder[TaxId] = JsonEncoder[String].contramap(TaxId.unwrap)
    implicit val jsonDecoder: JsonDecoder[TaxId] = JsonDecoder[String].map(TaxId.wrap)
    implicit val schema: Schema[TaxId] = Schema.primitive[String].transform(str => TaxId(str), tp => TaxId.unwrap(tp))
  }

  type CompanyName = CompanyName.Type

  object CompanyName extends Newtype[String] {
    implicit val eq: Equal[CompanyName] = Equal.default
    implicit val jsonEncoder: JsonEncoder[CompanyName] = JsonEncoder[String].contramap(CompanyName.unwrap)
    implicit val jsonDecoder: JsonDecoder[CompanyName] = JsonDecoder[String].map(CompanyName.wrap)
    implicit val schema: Schema[CompanyName] = Schema.primitive[String].transform(str => CompanyName(str), tp => CompanyName.unwrap(tp))
  }

  type StatementId = StatementId.Type
  object StatementId extends Newtype[String] {
    implicit val eq: Equal[StatementId] = Equal.default
    implicit val jsonEncoder: JsonEncoder[StatementId] = JsonEncoder[String].contramap(StatementId.unwrap)
    implicit val jsonDecoder: JsonDecoder[StatementId] = JsonDecoder[String].map(StatementId.wrap)
    implicit val schema: Schema[StatementId] = Schema.primitive[String].transform(str => StatementId(str), tp => StatementId.unwrap(tp))
  }
}
