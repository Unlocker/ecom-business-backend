package com.ecom.point.share.repos

import com.ecom.point.share.entities.{AccessToken, AccessTokenId, ExpirationTokenDate, RefreshToken, UserId}

final case class TokenDbo(
													 id: AccessTokenId.Type,
													 accessToken: AccessToken.Type,
													 refreshToken: RefreshToken.Type,
													 expirationTokenDate: ExpirationTokenDate.Type,
													 userId: UserId.Type
												 )
