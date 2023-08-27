package com.ecom.point.share

import com.ecom.point.utils.types._

package object entities {
	
	object UserId extends UUIDType
	type UserId = UserId.Type
	
	object AccessTokenId extends StringType
	type AccessTokenId = AccessTokenId.Type
	
	object AccessToken extends StringType
	type AccessToken = AccessToken.Type
	
	object RefreshToken extends StringType
	type RefreshToken = RefreshToken.Type
	
	object ExpirationTokenDate extends InstantType
	type ExpirationTokenDate = ExpirationTokenDate.Type
}
