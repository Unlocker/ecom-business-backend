package com.ecom.point.users

import com.ecom.point.utils.types._

package object entities {
	
	object PhoneNumber extends StringType
	type PhoneNumber = PhoneNumber.Type
	
	object Password extends StringType
	type Password  = Password.Type
	
	object Name extends StringType
	type Name = Name.Type
	
	object ActivateDate extends InstantType
	type ActivateDate = ActivateDate.Type
	
	object BlockDate extends InstantType
	type BlockDate = BlockDate.Type
	
	object CreatedDate extends InstantType
	type CreatedDate = CreatedDate.Type
	
	object LastLoginDate extends InstantType
	type LastLoginDate = LastLoginDate.Type
}
