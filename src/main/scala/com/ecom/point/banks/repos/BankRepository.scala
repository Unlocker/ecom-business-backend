package com.ecom.point.banks.repos

import com.ecom.point.banks.models.BankAccessToken
import com.ecom.point.configs.QuillContext._
import com.ecom.point.share.entities.{AccessTokenId, UserId}
import com.ecom.point.utils.RepositoryError
import com.ecom.point.utils.SchemeConverter._
import zio.{IO, Task, ZEnvironment, ZLayer}

import javax.sql.DataSource

trait BankRepository {
	def createBankAccessToken(bankAccessToken: BankAccessToken): IO[RepositoryError, BankAccessToken]
	
	def deleteBankAccessToken(tokenId: AccessTokenId.Type): IO[RepositoryError, Int]
	
	def getBankAccessTokens: Task[Seq[BankAccessToken]]
	
	def getBankAccessTokenById(tokenId: AccessTokenId.Type): Task[Option[BankAccessToken]]
	
	def getBankAccessTokenByUserId(usertId: UserId.Type): Task[Option[BankAccessToken]]
	
	def updateBankAccessToken(bankAccessToken: BankAccessToken): IO[RepositoryError, BankAccessToken]
}

object BankRepository {
	val layer: ZLayer[DataSource, Nothing, TokenRepositoryImpl] = ZLayer.fromFunction(TokenRepositoryImpl.apply _)
}

final case class TokenRepositoryImpl(dataSource: DataSource) extends BankRepository {
	private val envDataSource: ZEnvironment[DataSource] = zio.ZEnvironment(dataSource)
	
	override def createBankAccessToken(token: BankAccessToken): IO[RepositoryError, BankAccessToken] =
		run(Queries.createBankAccessToken(token))
			.provideEnvironment(envDataSource)
			.asModelWithMapError(RepositoryError(_))
	
	override def deleteBankAccessToken(tokenId: AccessTokenId.Type): IO[RepositoryError, Index] = {
		run(Queries.deleteBankAccessToken(tokenId))
			.provideEnvironment(envDataSource)
			.asModelWithMapError(RepositoryError(_))
	}
	
	override def getBankAccessTokens: Task[Seq[BankAccessToken]] = {
		run(Queries.getBankAccessTokens)
			.provideEnvironment(envDataSource)
			.asModel
	}
	
	override def getBankAccessTokenById(tokenId: AccessTokenId.Type): Task[Option[BankAccessToken]] = {
		run(Queries.getBankAccessTokenById(tokenId))
			.provideEnvironment(envDataSource)
			.map(_.headOption)
			.asModel
	}
	
	override def getBankAccessTokenByUserId(userId: UserId.Type): Task[Option[BankAccessToken]] = {
		run(Queries.getBankAccessTokenByUserId(userId))
			.provideEnvironment(envDataSource)
			.map(_.headOption)
			.asModel
	}
	
	override def updateBankAccessToken(bankAccessToken: BankAccessToken): IO[RepositoryError, BankAccessToken] = {
		run(Queries.updateBankAccessToken(bankAccessToken))
			.provideEnvironment(envDataSource)
			.asModelWithMapError(RepositoryError(_))
	}
}
