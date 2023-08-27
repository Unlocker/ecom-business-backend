package com.ecom.point.banks.models


import com.ecom.point.share.types._
import zio.json.{DeriveJsonCodec, JsonCodec}

import java.util.Currency

final case class BankAccountBalance(
                                     accountId: AccountId,
                                     bank: BankType,
//                                     amount: Currency
                                   )

object BankAccountBalance {
  implicit val tokenCodec: JsonCodec[BankAccountBalance] = DeriveJsonCodec.gen[BankAccountBalance]
}
