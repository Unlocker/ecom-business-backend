package com.ecom.point.share.repos

import com.ecom.point.configs.QuillContext._

import java.sql.Timestamp
import java.util.UUID

//object TokenDboContext {
//
//	object Decoders {
//		implicit val accessTokenIdDecoder: Decoder[AccessTokenId] = decoder((index, row, _) => AccessTokenId(UUID.fromString(row.getObject(index).toString)))
//		implicit val accessTokeDecoder: Decoder[AccessToken] = decoder((index, row, _) => AccessToken(row.getString(index)))
//		implicit val refreshTokenDecoder: Decoder[RefreshToken] = decoder((index, row, _) => RefreshToken(row.getString(index)))
//		implicit val expirationTokenDateDecoder: Decoder[ExpirationTokenDate] = decoder((index, row, _) => ExpirationTokenDate(row.getTimestamp(index).toInstant))
//
//	}
//
//	object Encoders {
//		implicit val accessTokenIdEncoder: Encoder[AccessTokenId] = encoder(java.sql.Types.OTHER, (index, value, row) => row.setObject(index, AccessTokenId.unwrap(value), java.sql.Types.OTHER))
//		implicit val accessTokeEncoder: Encoder[AccessToken] = encoder(java.sql.Types.VARCHAR, (index, value, row) => row.setString(index, AccessToken.unwrap(value)))
//		implicit val refreshTokenEncoder: Encoder[RefreshToken] = encoder(java.sql.Types.VARCHAR, (index, value, row) => row.setString(index, RefreshToken.unwrap(value)))
//		implicit val expiresTokenDateEncoder: Encoder[ExpirationTokenDate] = encoder(java.sql.Types.TIMESTAMP, (index, value, row) => row.setTimestamp(index, Timestamp.from(ExpirationTokenDate.unwrap(value))))
//	}
//
//}
