package com.zhongan.telecom.fp.use.reader

import java.util.Date

import com.zhongan.telecom.fp.use.reader.common.Amount

import scala.util.Try

//参数注入
trait AccountService[Account, Amount, Quota] {
  def open(no: String, name: String, openingDate: Option[Date], repo: AccountRepository): Try[Account]
  def debit(no: String, amount: Amount, repo: AccountRepository): Try[Account]
  def credit(no: String, amount: Amount, repo: AccountRepository): Try[Account]
  def balance(no: String, repo: AccountRepository): Try[Quota]
}

//延迟注入服务
trait AccountServiceLazy[Account, Amount, Quota] {
  def open(no: String, name: String, openingDate: Option[Date]): Try[Account]

  def debit(account: Account, amount: Amount): Try[Account]

  def credit(account: Account, amount: Amount): Try[Account]

  def balance(account: Account): Try[Quota]

  def transfer(from: Account, to: Account, amount: Amount): Try[(Account, Account, Amount)] = for {
    a <- debit(from, amount)
    b <- credit(to, amount)
  } yield (a, b, amount)
}




//使用Reader Monad
trait AccountServiceMonad[Account, Amount, Quota] {
  def open(no: String, name: String, openingDate: Option[Date]): Reader[AccountRepository, Try[Account]]
  def debit(no: String, amount: Amount): Reader[AccountRepository, Try[Account]]
  def credit(no: String, amount: Amount): Reader[AccountRepository, Try[Account]]
  def balance(no: String): Reader[AccountRepository, Try[Quota]]
}
object AccountServiceMonad extends AccountServiceMonad[Account, Amount, Quota] {

  //开户
  override def open(no: String, name: String, openingDate: Option[Date]): Reader[AccountRepository, Try[Account]] = ???
  //消费出纳
  override def debit(no: String, amount: Amount): Reader[AccountRepository, Try[Account]] = ???

  override def credit(no: String, amount: Amount): Reader[AccountRepository, Try[Account]] = ???
  //查询余额
  override def balance(no: String): Reader[AccountRepository, Try[Quota]] = ???
}