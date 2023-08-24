package com.ecom.point.share.repos

import com.ecom.point.share.entities.{AccessToken, AccessTokenId, ExpirationTokenDate, RefreshToken}
import io.getquill.MappedEncoding

import java.time.Instant
import java.util.UUID

object TokenDboContext {
	
	trait Decoders {
		implicit val accessTokenIdDecoder: MappedEncoding[UUID, AccessTokenId] = MappedEncoding[UUID, AccessTokenId](x => AccessTokenId(x))
		implicit val AccessTokeDecoder: MappedEncoding[String, AccessToken] = MappedEncoding[String, AccessToken](x => AccessToken(x))
		implicit val refreshTokenDecoder: MappedEncoding[String, RefreshToken] = MappedEncoding[String, RefreshToken](x => RefreshToken(x))
		implicit val expirationTokenDateDecoder: MappedEncoding[Instant, ExpirationTokenDate] = MappedEncoding[Instant, ExpirationTokenDate](x => ExpirationTokenDate(x))
		
	}
	
	trait Encoders {
		implicit val accessTokenIdEncoder: MappedEncoding[AccessTokenId, UUID] = MappedEncoding[AccessTokenId, UUID](x => AccessTokenId.unwrap(x))
		implicit val AccessTokeEncoder: MappedEncoding[AccessToken, String] = MappedEncoding[AccessToken, String](x => AccessToken.unwrap(x))
		implicit val refreshTokenEncoder: MappedEncoding[RefreshToken, String] = MappedEncoding[RefreshToken, String](x => RefreshToken.unwrap(x))
		implicit val expiresTokenDateEncoder: MappedEncoding[ExpirationTokenDate, Instant] = MappedEncoding[ExpirationTokenDate, Instant](x => ExpirationTokenDate.unwrap(x))
	}
	
}
