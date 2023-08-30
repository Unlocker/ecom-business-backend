package com.ecom.point.banks.services

import com.ecom.point.banks.models.{BankAccessToken, BankAccountBalance, BankStatement}
import com.ecom.point.share.types.AccountId
import com.ecom.point.users.models.User
import com.ecom.point.utils.AppError
import zio.IO

import java.net.URI
import java.time.LocalDate

trait TochkaBankService {
  def authorize(user: User): IO[AppError, Option[URI]]

  def fetchToken(user: User, code: String): IO[AppError, BankAccessToken]

  def refreshToken(user: User, token: BankAccessToken): IO[AppError, BankAccessToken]

  def balances(user: User, token: BankAccessToken): IO[AppError, List[BankAccountBalance]]

  def statements(user: User, token: BankAccessToken, accountId: AccountId, start: LocalDate, end: LocalDate): IO[AppError, List[BankStatement]]
}
