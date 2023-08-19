package com.ecom.point.configs

import io.getquill.context.sql.SqlContext
import io.getquill.context.json.PostgresJsonExtensions
import io.getquill.jdbczio.Quill
import io.getquill._
import io.getquill.{ PostgresZioJdbcContext, SnakeCase}
import zio.ZLayer

import java.time.{Instant, LocalDate}
import java.util.UUID
import javax.sql.DataSource
import com.ecom.point.banks.{TokenDbo => BankContext}
import com.ecom.point.users.{AccountDbo => UserContext}

trait Decoders extends BankContext.Decoders with UserContext.Decoders
trait Encoders extends BankContext.Encoders with UserContext.Encoders

trait Quotes {
	this: SqlContext[_, _] =>
	implicit class InstantContext (left: Instant) {
		def >(right: Instant) = quote(sql"$left > $right".as[Boolean])
		def <(right: Instant) = quote(sql"$left < $right".as[Boolean])
		def >=(right: Instant) = quote(sql"$left >= $right".as[Boolean])
		def <=(right: Instant) = quote(sql"$left <= $right".as[Boolean])
	}
	
	implicit class LocalDateContext (left: LocalDate) {
		def >(right: LocalDate) = quote(sql"$left > $right".as[Boolean])
		def <(right: LocalDate) = quote(sql"$left < $right".as[Boolean])
		def >=(right: LocalDate) = quote(sql"$left >= $right".as[Boolean])
		def <=(right: LocalDate) = quote(sql"$left <= $right".as[Boolean])
		def >(right: Option[LocalDate]) = quote(sql"$left < $right".as[Boolean])
		def <(right: Option[LocalDate]) = quote(sql"$left < $right".as[Boolean])
		def >=(right: Option[LocalDate]) = quote(sql"$left >= $right".as[Boolean])
		def <=(right: Option[LocalDate]) = quote(sql"$left <= $right".as[Boolean])
	}
	
	implicit class OptionLocalDateContext (left: Option[LocalDate]) {
		def >(right: LocalDate) = quote(sql"$left > $right".as[Boolean])
		def <(right: LocalDate) = quote(sql"$left < $right".as[Boolean])
		def <=(right: LocalDate) = quote(sql"$left < $right".as[Boolean])
		def >=(right: LocalDate) = quote(sql"$left >= $right".as[Boolean])
		def >(right: Option[LocalDate]) = quote(sql"$left > $right".as[Boolean])
		def <(right: Option[LocalDate]) = quote(sql"$left < $right".as[Boolean])
		def >=(right: Option[LocalDate]) = quote(sql"$left >= $right".as[Boolean])
		def <=(right: Option[LocalDate]) = quote(sql"$left <= $right".as[Boolean])
	}
}

object QuillContext extends PostgresZioJdbcContext(SnakeCase) with Encoders with Decoders with Quotes with PostgresJsonExtensions {
	val layer: ZLayer[Any, Nothing, DataSource] = Quill.DataSource.fromPrefix("database").orDie
	
}

