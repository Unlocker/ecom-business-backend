package com.ecom.point.utils

import com.ecom.point.configs.QuillContext
import org.postgresql.util.PSQLException

trait AppError extends Exception


trait RepositoryError extends AppError
object RepositoryError {
	def apply(error: QuillContext.Error): RepositoryError = {
		error match {
			case c: PSQLException if c.getErrorCode == 23505 => PhoneNumberMustBeUnique(c.getErrorCode, "Указанный номер зарегистрирован на другое лицо в нашей системе")
		}
	}
	final case class PhoneNumberMustBeUnique(code: Int, message: String) extends RepositoryError
	
}




trait ServiceError extends AppError
final case class Unauthorized(code: Int = 501, message: String = "Unauthorized") extends ServiceError


	

	
	

	

