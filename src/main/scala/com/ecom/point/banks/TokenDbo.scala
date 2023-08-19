package com.ecom.point.banks

import com.ecom.point.banks.Entities._
import com.ecom.point.users.Entities.AccountId
import io.getquill.MappedEncoding

import java.util.UUID
import java.time._


object TokenDbo {
	
	trait Decoders {
		implicit val accessTokenIdDecoder: MappedEncoding[UUID, AccessTokenId.Type] = MappedEncoding[UUID, AccessTokenId.Type](x => AccessTokenId(x))
		implicit val AccessTokeDecoder: MappedEncoding[String, AccessToken.Type] = MappedEncoding[String, AccessToken.Type](x => AccessToken(x))
		implicit val refreshTokenDecoder: MappedEncoding[String, RefreshToken.Type] = MappedEncoding[String, RefreshToken.Type](x => RefreshToken(x))
		implicit val cancelAccessTokenDateDecoder: MappedEncoding[Instant, CancelAccessTokenDate.Type] = MappedEncoding[Instant, CancelAccessTokenDate.Type](x => CancelAccessTokenDate(x))
		
	}
	
	trait Encoders {
		implicit val accessTokenIdEncoder: MappedEncoding[AccessTokenId.Type, UUID] = MappedEncoding[AccessTokenId.Type, UUID](x => AccessTokenId.unwrap(x))
		implicit val AccessTokeEncoder: MappedEncoding[AccessToken.Type, String] = MappedEncoding[AccessToken.Type, String](x => AccessToken.unwrap(x))
		implicit val refreshTokenEncoder: MappedEncoding[RefreshToken.Type, String] = MappedEncoding[RefreshToken.Type, String](x => RefreshToken.unwrap(x))
		implicit val cancelAccessTokenDateEncoder: MappedEncoding[CancelAccessTokenDate.Type, Instant] = MappedEncoding[CancelAccessTokenDate.Type, Instant](x => CancelAccessTokenDate.unwrap(x))
	}
	case class TokenDbo(
									id: AccessTokenId.Type,
									accessToken: AccessToken.Type,
									refreshToken: RefreshToken.Type,
									cancelAccessTokenDate: CancelAccessTokenDate.Type,
									accountId: AccountId.Type
									)
}
