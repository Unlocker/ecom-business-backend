package com.ecom.point.users.repos

import com.ecom.point.configs.QuillContext._
import com.ecom.point.share.entities.UserId
import com.ecom.point.users.entities._

import java.sql.Timestamp
import java.util.UUID

//object UserDboContext {
//	object Decoders {
//		implicit val userIdDecoder: Decoder[UserId] = decoder((index, row, _) => UserId(UUID.fromString(row.getObject(index).toString)))
//		implicit val phoneNumberDecoder: Decoder[PhoneNumber] = decoder((index, row, _) => PhoneNumber(row.getString(index)))
//		implicit val nameDecoder: Decoder[Name] = decoder((index, row, _) => Name(row.getString(index)))
//		implicit val passwordDecoder: Decoder[Password] = decoder((index, row, _) => Password(row.getString(index)))
//		implicit val activateDateDecoder: Decoder[ActivateDate] = decoder((index, row, _) => ActivateDate(row.getTimestamp(index).toInstant))
//		implicit val blockDateDecoder: Decoder[BlockDate] = decoder((index, row, _) => BlockDate(row.getTimestamp(index).toInstant))
//		implicit val createdAtDecoder: Decoder[CreatedDate] = decoder((index, row, _) => CreatedDate(row.getTimestamp(index).toInstant))
//		implicit val lastLoginDateDecoder: Decoder[LastLoginDate] = decoder((index, row, _) => LastLoginDate(row.getTimestamp(index).toInstant))
//	}
//
//	object Encoders {
//		implicit val userIdEncoder: Encoder[UserId] = encoder(java.sql.Types.OTHER, (index, value, row) => row.setObject(index, UserId.unwrap(value), java.sql.Types.OTHER))
//		implicit val phoneNumberEncoder: Encoder[PhoneNumber] = encoder(java.sql.Types.VARCHAR, (index, value, row) => row.setString(index, PhoneNumber.unwrap(value)))
//		implicit val nameEncoder: Encoder[Name] = encoder(java.sql.Types.VARCHAR, (index, value, row) => row.setString(index, Name.unwrap(value)))
//		implicit val passwordEncoder: Encoder[Password] = encoder(java.sql.Types.VARCHAR, (index, value, row) => row.setString(index, Password.unwrap(value)))
//		implicit val activateDateEncoder: Encoder[ActivateDate] = encoder(java.sql.Types.TIMESTAMP, (index, value, row) => row.setTimestamp(index, Timestamp.from(ActivateDate.unwrap(value))))
//		implicit val blockDateEncoder: Encoder[BlockDate] = encoder(java.sql.Types.TIMESTAMP, (index, value, row) => row.setTimestamp(index, Timestamp.from(BlockDate.unwrap(value))))
//		implicit val createdAtEncoder: Encoder[CreatedDate] = encoder(java.sql.Types.TIMESTAMP, (index, value, row) => row.setTimestamp(index, Timestamp.from(CreatedDate.unwrap(value))))
//		implicit val lastLoginDateEncoder: Encoder[LastLoginDate] = encoder(java.sql.Types.TIMESTAMP, (index, value, row) => row.setTimestamp(index, Timestamp.from(LastLoginDate.unwrap(value))))
//	}
//}
