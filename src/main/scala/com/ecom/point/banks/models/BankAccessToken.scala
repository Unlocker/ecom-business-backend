package com.ecom.point.banks.models

import com.ecom.point.share.entities.{AccessToken, AccessTokenId, ExpirationTokenDate, RefreshToken, UserId}
import com.ecom.point.share.repos.TokenDbo
import zio.json.{DeriveJsonCodec, JsonCodec}

final case class BankAccessToken(
												id: AccessTokenId.Type,
												accessToken: AccessToken.Type,
												refreshToken: RefreshToken.Type,
												expirationTokenDate: ExpirationTokenDate.Type,
												userId: UserId.Type
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
