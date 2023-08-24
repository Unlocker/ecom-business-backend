package com.ecom.point.users.models

import com.ecom.point.share.entities._
import com.ecom.point.share.repos.TokenDbo
import com.ecom.point.users.entities._
import com.ecom.point.users.repos.UserDbo
import com.ecom.point.users.repos.UserDbo._
import zio.json.{DeriveJsonCodec, JsonCodec}

final case class User(
											 id: UserId,
													phoneNumber: PhoneNumber,
													name: Name,
													password: Password,
													activateDate: Option[ActivateDate],
													blockDate: Option[BlockDate],
													createdAt: CreatedDate,
													lastLoginDate: Option[LastLoginDate]
												)

object User {
	implicit val converterFromDbo: UserDbo => User = {
		dbo =>
			User(
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
	implicit val jsonCodec: JsonCodec[User] = DeriveJsonCodec.gen[User]
}


final case class UserAccessToken(
																	id: AccessTokenId,
																	accessToken: AccessToken,
																	refreshToken: RefreshToken,
																	expirationTokenDate: ExpirationTokenDate,
																	userId: UserId
																)

object UserAccessToken {
	implicit val converterFromDbo: TokenDbo => UserAccessToken = {
		dbo =>
			UserAccessToken(
				id = dbo.id,
				accessToken = dbo.accessToken,
				refreshToken = dbo.refreshToken,
				expirationTokenDate = dbo.expirationTokenDate,
				userId = dbo.userId
			)
	}
	implicit val jsonCodec: JsonCodec[UserAccessToken] = DeriveJsonCodec.gen[UserAccessToken]
}