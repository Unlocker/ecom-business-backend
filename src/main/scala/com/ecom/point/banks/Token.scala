package com.ecom.point.banks

import com.ecom.point.banks.Entities._
import com.ecom.point.users.Entities.AccountId
import zio.json.{DeriveJsonCodec, JsonCodec}

final case class Token(
												id: AccessTokenId.Type,
												accessToken: AccessToken.Type,
												refreshToken: RefreshToken.Type,
												cancelAccessTokenDate: CancelAccessTokenDate.Type,
												accountId: AccountId.Type
											)

object Token {
	implicit val tokenCodec: JsonCodec[Token] = DeriveJsonCodec.gen[Token]
}
