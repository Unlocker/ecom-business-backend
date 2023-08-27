package com.ecom.point.users.endpoints
import zio.prelude._
import com.ecom.point.share.types._
import zio.schema.{DeriveSchema, Schema}



object EndpointData {
	final case class SignUpRequest(phoneNumber: PhoneNumber, name: Name, password: Password, passwordAgain: Password)
	object SignUpRequest {
		implicit val signUpRequestSchema: Schema[SignUpRequest] = DeriveSchema.gen[SignUpRequest]
	}
	
	final case class SignInRequest(phoneNumber: PhoneNumber, password: Password)
	object SignInRequest {
		implicit val signUpRequestSchema: Schema[SignInRequest] = DeriveSchema.gen[SignInRequest]
	}
	
	final case class SignInUpResponse(userAccessJwtToken: AccessToken)
	object SignInUpResponse {
		implicit val signUpRequestSchema: Schema[SignInUpResponse] = DeriveSchema.gen[SignInUpResponse]
	}
	
}
