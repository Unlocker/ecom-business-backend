package com.ecom.point.banks.models

import com.ecom.point.banks.entities.{BankType, StatementDirection}
import com.ecom.point.banks.entities.{AccountId, BankType, StatementDirection, StatementId}
import zio.json.{DeriveJsonCodec, JsonCodec}

import java.time.LocalDate
import java.util.Currency

case class BankStatement(
                          statementId: StatementId,
                          accountId: AccountId,
                          bank: BankType,
                          direction: StatementDirection,
                          date: LocalDate,
                          counterparty: Counterparty,
//                          amount: Currency
                        )

object BankStatement {
  implicit val tokenCodec: JsonCodec[BankStatement] = DeriveJsonCodec.gen[BankStatement]
}