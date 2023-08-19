package com.ecom.point.users

import com.ecom.point.users.AccountDbo.AccountDbo
import com.ecom.point.users.Entities._
import zio.json.{DeriveJsonCodec, DeriveJsonEncoder, JsonCodec, JsonEncoder}

final case class Account(
													id: AccountId.Type,
													phoneNumber: PhoneNumber.Type,
													name: Name.Type,
													password: Password.Type,
													activateDate: Option[ActivateDate.Type],
													blockDate: Option[BlockDate.Type],
													createdAt: CreatedDate.Type,
													lastLoginDate: Option[LastLoginDate.Type]
												)

object Account {
	implicit val converterFromDbo: AccountDbo => Account = {
		dbo =>
			Account(
				id = dbo.id,
				phoneNumber = dbo.phoneNumber,
				name = dbo.name,
				password = dbo.password,
				activateDate = dbo.activateDate,
				blockDate = dbo.blockDate,
				createdAt = dbo.createdAt,
				lastLoginDate = dbo.lastLoginDate
			)
	}
	implicit val jsonCodec: JsonCodec[Account] = DeriveJsonCodec.gen[Account]
}
