package com.ecom.point.banks.models

import com.ecom.point.share.types._
import zio.json.{DeriveJsonCodec, JsonCodec}

import java.time.LocalDate

case class BankTransaction(
                          transactionId: TransactionId,
                          accountId: AccountId,
                          bank: BankType,
                          direction: StatementDirection,
                          date: LocalDate,
                          counterparty: Counterparty,
                          amount: Money
                        )

object BankTransaction {
  implicit val tokenCodec: JsonCodec[BankTransaction] = DeriveJsonCodec.gen[BankTransaction]
}