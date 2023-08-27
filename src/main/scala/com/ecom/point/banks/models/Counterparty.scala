package com.ecom.point.banks.models

import com.ecom.point.share.types._
import zio.json.{DeriveJsonCodec, JsonCodec}

final case class Counterparty(
                               name: CompanyName,
                               taxId: TaxId
                             )

object Counterparty {
  implicit val tokenCodec: JsonCodec[Counterparty] = DeriveJsonCodec.gen[Counterparty]
}
