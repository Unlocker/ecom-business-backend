package com.ecom.point.banks

import com.ecom.point.banks.Entities._
import zio.{IO, Task, ZEnvironment, ZLayer}
import com.ecom.point.configs.QuillContext._
import com.ecom.point.users.Entities.AccountId
import com.ecom.point.utils.SchemeConverter._

import javax.sql.DataSource

trait TokenRepository {
	def createToken(token: Token): IO[RepositoryError, Token]
	def deleteToken(tokenId: AccessTokenId.Type): IO[RepositoryError, Int]
	def getTokens: Task[Seq[Token]]
	def getTokenById(tokenId: AccessTokenId.Type): Task[Option[Token]]
	def getTokenByAccountId(accountId: AccountId.Type): Task[Option[Token]]
	def updateToken(account: Token): IO[RepositoryError, Token]
}

object TokenRepository {
	val layer = ZLayer.fromFunction(TokenRepositoryImpl.apply _)
}

final case class TokenRepositoryImpl(dataSource: DataSource) extends TokenRepository {
	private val envDataSource: ZEnvironment[DataSource] = zio.ZEnvironment(dataSource)
	
	override def createToken(token: Token): IO[Any, Token] = ???
	
	override def deleteToken(tokenId: AccessTokenId.Type): IO[Any, Index] = ???
	
	override def getTokens: Task[Seq[Token]] = ???
	
	override def getTokenById(tokenId: AccessTokenId.Type): Task[Option[Token]] = ???
	
	override def getTokenByAccountId(accountId: AccountId.Type): Task[Option[Token]] = ???
	
	override def updateToken(account: Token): IO[Any, Token] = ???
}
