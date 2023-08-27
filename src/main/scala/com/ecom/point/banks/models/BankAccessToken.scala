package com.ecom.point.banks.models

import com.ecom.point.share.types._
import com.ecom.point.share.repos.TokenDbo
import zio.json.{DeriveJsonCodec, JsonCodec}

final case class BankAccessToken(
												id: AccessTokenId,
												accessToken: AccessToken,
												refreshToken: RefreshToken,
												expirationTokenDate: ExpirationTokenDate,
												userId: UserId
											)

object BankAccessToken {
	implicit val converterFromDbo: TokenDbo => BankAccessToken = {
		dbo =>
			BankAccessToken(
				id = dbo.id,
				accessToken = dbo.accessToken,
				refreshToken = dbo.refreshToken,
				expirationTokenDate = dbo.expirationTokenDate,
				userId = dbo.userId
			)
	}
	implicit val tokenCodec: JsonCodec[BankAccessToken] = DeriveJsonCodec.gen[BankAccessToken]
}
