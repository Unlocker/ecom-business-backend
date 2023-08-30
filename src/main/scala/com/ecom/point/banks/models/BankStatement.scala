package com.ecom.point.banks.models

import com.ecom.point.share.types._
import zio.json.{DeriveJsonCodec, JsonCodec}

import java.time.LocalDate

case class BankStatement(
                          statementId: StatementId,
                          accountId: AccountId,
                          bank: BankType,
                          direction: StatementDirection,
                          date: LocalDate,
                          counterparty: Counterparty,
                          amount: Money
                        )

object BankStatement {
  implicit val tokenCodec: JsonCodec[BankStatement] = DeriveJsonCodec.gen[BankStatement]
}