package com.ecom.point.banks.services

import com.ecom.point.banks.models.{BankAccessToken, BankAccountBalance, BankStatement}
import com.ecom.point.banks.repos.BankRepository
import com.ecom.point.share.types.AccountId
import com.ecom.point.users.models.User
import com.ecom.point.utils.AppError
import zio.{IO, ZLayer}
import zio.http.Client

import java.net.URI
import java.time.LocalDate

trait TochkaBankService {
  def authorize(user: User): IO[AppError, Option[URI]]

  def fetchToken(user: User, code: String): IO[AppError, BankAccessToken]

  def refreshToken(user: User, token: BankAccessToken): IO[AppError, BankAccessToken]

  def balances(user: User, token: BankAccessToken): IO[AppError, List[BankAccountBalance]]

  def statements(user: User, token: BankAccessToken, accountId: AccountId, start: LocalDate, end: LocalDate): IO[AppError, List[BankStatement]]
}

object TochkaBankService{
  def layer: ZLayer[Client with BankRepository, Nothing, TochkaBankServiceLive] = ZLayer.fromFunction(TochkaBankServiceLive.apply _)
}

final case class TochkaBankServiceLive(client: Client, bankRepository: BankRepository) extends TochkaBankService {
  override def authorize(user: User): IO[AppError, Option[URI]] = ???
  
  override def fetchToken(user: User, code: String): IO[AppError, BankAccessToken] = ???
  
  override def refreshToken(user: User, token: BankAccessToken): IO[AppError, BankAccessToken] = ???
  
  override def balances(user: User, token: BankAccessToken): IO[AppError, List[BankAccountBalance]] = ???
  
  override def statements(user: User, token: BankAccessToken, accountId: AccountId, start: LocalDate, end: LocalDate): IO[AppError, List[BankStatement]] = ???
}
