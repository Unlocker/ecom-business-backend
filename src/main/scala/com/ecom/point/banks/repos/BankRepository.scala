package com.ecom.point.banks.repos

import com.ecom.point.banks.models.BankAccessToken
import com.ecom.point.share.types._
import com.ecom.point.utils.RepositoryError
import com.ecom.point.utils.SchemeConverter._
import io.getquill.SnakeCase
import io.getquill.jdbczio.Quill
import zio.{IO, Task, ZLayer}

trait BankRepository {
	def createBankAccessToken(bankAccessToken: BankAccessToken): IO[RepositoryError, BankAccessToken]

	def deleteBankAccessToken(tokenId: AccessTokenId): IO[RepositoryError, Int]

	def getBankAccessTokens: Task[Seq[BankAccessToken]]
	
	def getBankAccessTokenById(tokenId: AccessTokenId): Task[Option[BankAccessToken]]
	
	def getBankAccessTokenByUserId(userId: UserId): Task[Option[BankAccessToken]]
	
	def updateBankAccessToken(bankAccessToken: BankAccessToken): IO[RepositoryError, BankAccessToken]
}

object BankRepository {
	val layer: ZLayer[Quill.Postgres[SnakeCase], Nothing, TokenRepositoryImpl] = ZLayer.fromFunction(TokenRepositoryImpl.apply _)
}

final case class TokenRepositoryImpl(dataSource: Quill.Postgres[SnakeCase]) extends BankRepository {
	
	import dataSource._
	
	override def createBankAccessToken(token: BankAccessToken): IO[RepositoryError, BankAccessToken] =
		run(Queries.createBankAccessToken(token))
			.asModelWithMapError(RepositoryError(_))
	
	override def deleteBankAccessToken(tokenId: AccessTokenId): IO[RepositoryError, Index] = {
		run(Queries.deleteBankAccessToken(tokenId))
			.asModelWithMapError(RepositoryError(_))
	}
	
	override def getBankAccessTokens: Task[Seq[BankAccessToken]] = {
		run(Queries.getBankAccessTokens)
			.asModel
	}
	
	override def getBankAccessTokenById(tokenId: AccessTokenId): Task[Option[BankAccessToken]] = {
		run(Queries.getBankAccessTokenById(tokenId))
			.map(_.headOption)
			.asModel
	}
	
	override def getBankAccessTokenByUserId(userId: UserId): Task[Option[BankAccessToken]] = {
		run(Queries.getBankAccessTokenByUserId(userId))
			.map(_.headOption)
			.asModel
	}
	
	override def updateBankAccessToken(bankAccessToken: BankAccessToken): IO[RepositoryError, BankAccessToken] = {
		run(Queries.updateBankAccessToken(bankAccessToken))
			.asModelWithMapError(RepositoryError(_))
	}
}
