package com.ecom.point.configs

import com.ecom.point.configs.QuillContext.MappedEncoding
import io.getquill.context.sql.SqlContext
import io.getquill.jdbczio.Quill
import io.getquill.{PostgresZioJdbcContext, Quoted, SnakeCase}
import zio.ZLayer
import zio.prelude.Equivalence
import com.ecom.point.utils.types._

import java.time.{Instant, LocalDate}
import javax.sql.DataSource

trait NewTypeMapping {
	implicit def newtypeEncoder[A, T <: RichNewtype[A]#Type](implicit equiv: Equivalence[A, T]): MappedEncoding[T, A] = MappedEncoding[T, A](RichNewtype.unwrap(_))
	implicit def newtypeDecoder[A, T <: RichNewtype[A]#Type](implicit equiv: Equivalence[A, T]): MappedEncoding[A, T] = MappedEncoding[A, T](RichNewtype.wrap(_))
}


trait Quotes {
	this: SqlContext[_, _] =>
	implicit class InstantContext (left: Instant) {
		def >(right: Instant): Quoted[Boolean] = quote(sql"$left > $right".as[Boolean])
		def <(right: Instant): Quoted[Boolean] = quote(sql"$left < $right".as[Boolean])
		def >=(right: Instant): Quoted[Boolean] = quote(sql"$left >= $right".as[Boolean])
		def <=(right: Instant): Quoted[Boolean] = quote(sql"$left <= $right".as[Boolean])
	}
	
	implicit class LocalDateContext (left: LocalDate) {
		def >(right: LocalDate): Quoted[Boolean] = quote(sql"$left > $right".as[Boolean])
		def <(right: LocalDate): Quoted[Boolean] = quote(sql"$left < $right".as[Boolean])
		def >=(right: LocalDate): Quoted[Boolean] = quote(sql"$left >= $right".as[Boolean])
		def <=(right: LocalDate): Quoted[Boolean] = quote(sql"$left <= $right".as[Boolean])
		def >(right: Option[LocalDate]): Quoted[Boolean] = quote(sql"$left < $right".as[Boolean])
		def <(right: Option[LocalDate]): Quoted[Boolean] = quote(sql"$left < $right".as[Boolean])
		def >=(right: Option[LocalDate]): Quoted[Boolean] = quote(sql"$left >= $right".as[Boolean])
		def <=(right: Option[LocalDate]): Quoted[Boolean] = quote(sql"$left <= $right".as[Boolean])
	}
	
	implicit class OptionLocalDateContext (left: Option[LocalDate]) {
		def >(right: LocalDate): Quoted[Boolean] = quote(sql"$left > $right".as[Boolean])
		def <(right: LocalDate): Quoted[Boolean] = quote(sql"$left < $right".as[Boolean])
		def <=(right: LocalDate): Quoted[Boolean] = quote(sql"$left < $right".as[Boolean])
		def >=(right: LocalDate): Quoted[Boolean] = quote(sql"$left >= $right".as[Boolean])
		def >(right: Option[LocalDate]): Quoted[Boolean] = quote(sql"$left > $right".as[Boolean])
		def <(right: Option[LocalDate]): Quoted[Boolean] = quote(sql"$left < $right".as[Boolean])
		def >=(right: Option[LocalDate]): Quoted[Boolean] = quote(sql"$left >= $right".as[Boolean])
		def <=(right: Option[LocalDate]): Quoted[Boolean] = quote(sql"$left <= $right".as[Boolean])
	}
}

object QuillContext extends PostgresZioJdbcContext(SnakeCase) with NewTypeMapping with Quotes {
	val layer: ZLayer[Any, Nothing, DataSource] = Quill.DataSource.fromPrefix("database").orDie
	
}

