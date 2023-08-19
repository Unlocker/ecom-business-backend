package com.ecom.point.users

import com.ecom.point.users.Entities._
import io.getquill.MappedEncoding
import java.time._
import java.util.UUID


object AccountDbo {
	trait Decoders {
		implicit val userIdDecoder: MappedEncoding[UUID, AccountId.Type] = MappedEncoding[UUID, AccountId.Type](x => AccountId(x))
		implicit val phoneNumberDecoder: MappedEncoding[String, PhoneNumber.Type] = MappedEncoding[String, PhoneNumber.Type](x => PhoneNumber(x))
		implicit val nameDecoder: MappedEncoding[String, Name.Type] = MappedEncoding[String, Name.Type](x => Name(x))
		implicit val passwordDecoder: MappedEncoding[String, Password.Type] = MappedEncoding[String, Password.Type](x => Password(x))
		implicit val activateDateDecoder: MappedEncoding[Instant, ActivateDate.Type] = MappedEncoding[Instant, ActivateDate.Type](x => ActivateDate(x))
		implicit val blockDateDecoder: MappedEncoding[Instant, BlockDate.Type] = MappedEncoding[Instant, BlockDate.Type](x => BlockDate(x))
		implicit val createdAtDecoder: MappedEncoding[Instant, CreatedDate.Type] = MappedEncoding[Instant, CreatedDate.Type](x => CreatedDate(x))
		implicit val lastLoginDateDecoder: MappedEncoding[Instant, LastLoginDate.Type] = MappedEncoding[Instant, LastLoginDate.Type](x => LastLoginDate(x))
	}
	
	trait Encoders {
		implicit val userIdEncoder: MappedEncoding[AccountId.Type, UUID] = MappedEncoding[AccountId.Type, UUID](x => AccountId.unwrap(x))
		implicit val phoneNumberEncoder: MappedEncoding[PhoneNumber.Type, String] = MappedEncoding[PhoneNumber.Type, String](x => PhoneNumber.unwrap(x))
		implicit val nameEncoder: MappedEncoding[Name.Type, String] = MappedEncoding[Name.Type, String](x => Name.unwrap(x))
		implicit val passwordEncoder: MappedEncoding[Password.Type, String] = MappedEncoding[Password.Type, String](x => Password.unwrap(x))
		implicit val activateDateEncoder: MappedEncoding[ActivateDate.Type, Instant] = MappedEncoding[ActivateDate.Type, Instant](x => ActivateDate.unwrap(x))
		implicit val blockDateEncoder: MappedEncoding[BlockDate.Type, Instant] = MappedEncoding[BlockDate.Type, Instant](x => BlockDate.unwrap(x))
		implicit val createdAtEncoder: MappedEncoding[CreatedDate.Type, Instant] = MappedEncoding[CreatedDate.Type, Instant](x => CreatedDate.unwrap(x))
		implicit val lastLoginDateEncoder: MappedEncoding[LastLoginDate.Type, Instant] = MappedEncoding[LastLoginDate.Type, Instant](x => LastLoginDate.unwrap(x))
	}
	
	final case class AccountDbo(
														id: AccountId.Type,
														phoneNumber: PhoneNumber.Type,
														name: Name.Type,
														password: Password.Type,
														activateDate: Option[ActivateDate.Type],
														blockDate: Option[BlockDate.Type],
														createdAt: CreatedDate.Type,
														lastLoginDate: Option[LastLoginDate.Type]
													)
}
