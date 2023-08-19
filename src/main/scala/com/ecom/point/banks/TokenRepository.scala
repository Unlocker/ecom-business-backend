package com.ecom.point.banks

import com.ecom.point.banks.Entities._
import zio.{IO, Task, ZEnvironment, ZLayer}
import com.ecom.point.configs.QuillContext._
import com.ecom.point.users.Entities.AccountId
import com.ecom.point.utils.SchemeConverter._
import Token._
import com.ecom.point.utils.Errors.RepositoryError

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
	
	override def createToken(token: Token): IO[RepositoryError, Token] =
		run(Queries.create(token))
			.provideEnvironment(envDataSource)
			.asModelWithMapError(err => RepositoryError(err.getErrorCode, err.getMessage))
	
	override def deleteToken(tokenId: AccessTokenId.Type): IO[RepositoryError, Index] = {
		run(Queries.delete(tokenId))
			.provideEnvironment(envDataSource)
			.asModelWithMapError(err => RepositoryError(err.getErrorCode, err.getMessage))
	}
	
	override def getTokens: Task[Seq[Token]] = {
		run(Queries.get)
			.provideEnvironment(envDataSource)
			.asModel
	}
	
	override def getTokenById(tokenId: AccessTokenId.Type): Task[Option[Token]] = {
		run(Queries.getById(tokenId))
			.provideEnvironment(envDataSource)
			.map(_.headOption)
			.asModel
	}
	
	override def getTokenByAccountId(accountId: AccountId.Type): Task[Option[Token]] = {
		run(Queries.getByAccountId(accountId))
			.provideEnvironment(envDataSource)
			.map(_.headOption)
			.asModel
	}
	
	override def updateToken(account: Token): IO[RepositoryError, Token] = {
		run(Queries.update(account))
			.provideEnvironment(envDataSource)
			.asModelWithMapError(err => RepositoryError(err.getErrorCode, err.getMessage))
	}
}
