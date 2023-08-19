package com.ecom.point.banks

import com.ecom.point.banks.Entities._
import com.ecom.point.banks.TokenDbo.TokenDbo
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
	implicit val converterFromDbo: TokenDbo => Token = {
		dbo =>
			Token(
				id = dbo.id,
				accessToken = dbo.accessToken,
				refreshToken = dbo.refreshToken,
				cancelAccessTokenDate = dbo.cancelAccessTokenDate,
				accountId = dbo.accountId
			)
	}
	implicit val tokenCodec: JsonCodec[Token] = DeriveJsonCodec.gen[Token]
}
