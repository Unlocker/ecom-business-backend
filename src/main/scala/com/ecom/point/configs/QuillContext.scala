package com.ecom.point.configs

import io.getquill.context.sql.SqlContext
import io.getquill.jdbczio.Quill
import io.getquill._
import zio.ZLayer

import java.time.{Instant, LocalDate}


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

object QuillContext extends PostgresZioJdbcContext(SnakeCase) {
	val layer: ZLayer[Any, Throwable, Quill.Postgres[SnakeCase.type]] =  Quill.DataSource.fromPrefix("database") >>> Quill.Postgres.fromNamingStrategy(SnakeCase)
}

