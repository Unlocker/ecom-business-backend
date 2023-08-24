package com.ecom.point.users.repos

import com.ecom.point.share.entities._
import com.ecom.point.users.entities._


final case class UserDbo(
													id: UserId,
													phoneNumber: PhoneNumber,
													name: Name,
													password: Password,
													activateDate: Option[ActivateDate],
													blockDate: Option[BlockDate],
													createdAt: CreatedDate,
													lastLoginDate: Option[LastLoginDate]
												)



