package com.ecom.point.utils

import com.ecom.point.configs.QuillContext

trait DBErrors[-E, +E2] extends Exception{
	def descriptionError: E => E2
}

object Errors {
	case class RepositoryError(code: Int = 0, message: String = "") extends DBErrors[QuillContext.Error, RepositoryError] {
		self =>
		
		override def descriptionError: QuillContext.Error => RepositoryError =
			err => self.copy(code = err.getErrorCode, message = err.getMessage)
		
	}
}
