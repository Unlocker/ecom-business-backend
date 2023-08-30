package com.ecom.point.banks.models

import zio.json.{DeriveJsonCodec, JsonCodec}
import com.ecom.point.share.types._

import java.util.Currency

final case class Money(amount: BigDecimal, currency: Currency)

object Money {
  implicit val moneyCodec: JsonCodec[Money] = DeriveJsonCodec.gen[Money]
}
