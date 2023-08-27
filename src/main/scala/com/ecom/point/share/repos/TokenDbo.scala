package com.ecom.point.share.repos

import com.ecom.point.share.types._

final case class TokenDbo(
													 id: AccessTokenId,
													 accessToken: AccessToken,
													 refreshToken: RefreshToken,
													 expirationTokenDate: ExpirationTokenDate,
													 userId: UserId
												 )
