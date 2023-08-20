package com.ecom.point.users.endpoints

import zio.http.codec.HttpCodec._
import zio.http.endpoint._
import zio.schema.{DeriveSchema, Schema}

object Endpoints {
	final case class SignUpRequest()
	
	object SignUpRequest {
		implicit val signUpRequestSchema: Schema[SignUpRequest] = DeriveSchema.gen[SignUpRequest]
	}
	
	final case class SignUpResponse()
	
	object SignUpResponse {
		implicit val signUpResponseSchema: Schema[SignUpResponse] = DeriveSchema.gen[SignUpResponse]
	}
	
	val signUp = Endpoint
		.post(literal("signup"))
		.in[SignUpRequest]
		.output[SignUpResponse]
}
