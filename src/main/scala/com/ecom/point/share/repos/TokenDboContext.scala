package com.ecom.point.share.repos

import com.ecom.point.share.entities.{AccessToken, AccessTokenId, ExpirationTokenDate, RefreshToken}
import io.getquill.MappedEncoding

import java.time.Instant
import java.util.UUID

object TokenDboContext {
	
	trait Decoders {
		implicit val accessTokenIdDecoder: MappedEncoding[UUID, AccessTokenId.Type] = MappedEncoding[UUID, AccessTokenId.Type](x => AccessTokenId(x))
		implicit val AccessTokeDecoder: MappedEncoding[String, AccessToken.Type] = MappedEncoding[String, AccessToken.Type](x => AccessToken(x))
		implicit val refreshTokenDecoder: MappedEncoding[String, RefreshToken.Type] = MappedEncoding[String, RefreshToken.Type](x => RefreshToken(x))
		implicit val expirationTokenDateDecoder: MappedEncoding[Instant, ExpirationTokenDate.Type] = MappedEncoding[Instant, ExpirationTokenDate.Type](x => ExpirationTokenDate(x))
		
	}
	
	trait Encoders {
		implicit val accessTokenIdEncoder: MappedEncoding[AccessTokenId.Type, UUID] = MappedEncoding[AccessTokenId.Type, UUID](x => AccessTokenId.unwrap(x))
		implicit val AccessTokeEncoder: MappedEncoding[AccessToken.Type, String] = MappedEncoding[AccessToken.Type, String](x => AccessToken.unwrap(x))
		implicit val refreshTokenEncoder: MappedEncoding[RefreshToken.Type, String] = MappedEncoding[RefreshToken.Type, String](x => RefreshToken.unwrap(x))
		implicit val expiresTokenDateEncoder: MappedEncoding[ExpirationTokenDate.Type, Instant] = MappedEncoding[ExpirationTokenDate.Type, Instant](x => ExpirationTokenDate.unwrap(x))
	}
	
}
