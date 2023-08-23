package com.ecom.point.users.endpoints

import com.ecom.point.share.entities.AccessToken
import com.ecom.point.users.entities.{Name, Password, PhoneNumber}
import zio.schema.{DeriveSchema, Schema}


object EndpointData {
	final case class SignUpRequest(phoneNumber: PhoneNumber.Type, name: Name.Type, password: Password.Type, passwordAgain: Password.Type)
	object SignUpRequest {
		implicit val signUpRequestSchema: Schema[SignUpRequest] = DeriveSchema.gen[SignUpRequest]
	}
	
	final case class SignInRequest(phoneNumber: PhoneNumber.Type, password: Password.Type)
	object SignInRequest {
		implicit val signInRequestSchema: Schema[SignInRequest] = DeriveSchema.gen[SignInRequest]
	}

	final case class SignInUpResponse(userAccessJwtToken: AccessToken.Type)
	object SignInUpResponse {
		implicit val signInUpResponseSchema: Schema[SignInUpResponse] = DeriveSchema.gen[SignInUpResponse]
	}
	
}
