package com.ecom.point.users.endpoints
import zio.prelude._
import com.ecom.point.share.types._
import zio.schema.{DeriveSchema, Schema}



object EndpointData {
	sealed trait ApiError
	object ApiError{
		implicit val phoneAlreadyUsedSchema: Schema[PhoneAlreadyUsed] = DeriveSchema.gen[PhoneAlreadyUsed]
		implicit val passwordsNotEqualSchema: Schema[PasswordsNotEqual] = DeriveSchema.gen[PasswordsNotEqual]
		implicit val passwordsOrPhoneHasNotSystem: Schema[PasswordsOrPhoneIncorrect] = DeriveSchema.gen[PasswordsOrPhoneIncorrect]
	}
	
	final case class PhoneAlreadyUsed(code: Int  = 150, msg: String = "Phone number already used other user" ) extends ApiError
	final case class PasswordsNotEqual(code: Int = 151, msg: String = "Passwords not equal") extends ApiError
	final case class PasswordsOrPhoneIncorrect(code: Int = 152, msg: String = "phone number or password not correctly") extends ApiError
	
	
	
	final case class SignUpRequest(phoneNumber: PhoneNumber, name: Name, password: Password, passwordAgain: Password)
	object SignUpRequest {
		implicit val signUpRequestSchema: Schema[SignUpRequest] = DeriveSchema.gen[SignUpRequest]
	}
	
	final case class SignInRequest(phoneNumber: PhoneNumber, password: Password)
	object SignInRequest {
		implicit val signUpRequestSchema: Schema[SignInRequest] = DeriveSchema.gen[SignInRequest]
	}
	
	final case class SignInResponse(accessToken: AccessToken, expirationTokenDate: ExpirationTokenDate)
	object SignInResponse {
		implicit val signUpResponseSchema: Schema[SignInResponse] = DeriveSchema.gen[SignInResponse]
	}
}
