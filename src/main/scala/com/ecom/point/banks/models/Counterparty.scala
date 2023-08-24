package com.ecom.point.banks.models

import com.ecom.point.banks.entities.{CompanyName, TaxId}
import zio.json.{DeriveJsonCodec, JsonCodec}

final case class Counterparty(
                               name: CompanyName,
                               taxId: TaxId
                             )

object Counterparty {
  implicit val tokenCodec: JsonCodec[Counterparty] = DeriveJsonCodec.gen[Counterparty]
}
