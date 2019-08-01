package com.zhongan.telecom.fp.use.reader

import java.util.Date

import cats.data.Kleisli
import com.zhongan.telecom.fp.use.reader.common.{Amount, today}

import scala.util.{Failure, Success, Try}

/**
  * Eager 注入服务
  * @tparam Account
  * @tparam Amount
  * @tparam Quota
  */
trait AccountService[Account, Amount, Quota] {
  //开户
  def open(no: String, name: String, openingDate: Option[Date], repo: AccountRepository): Try[Account]
  //取钱
  def debit(no: String, amount: Amount, repo: AccountRepository): Try[Account]
  //存钱
  def credit(no: String, amount: Amount, repo: AccountRepository): Try[Account]
  //余额
  def balance(no: String, repo: AccountRepository): Try[Quota]
}

object AccountService extends AccountService[Account, Amount, Quota] {

  //开户
  def open(no: String, name: String, openingDate: Option[Date], repo: AccountRepository): Try[Account] =
    repo.query(no) match {
      case Success(Some(a)) => Failure(new Exception(s"用户 $no 已存在"))
      case Success(None) =>
        if (no.isEmpty || name.isEmpty) Failure(new Exception(s"名字不能为空") )
        else repo.save(Account(no, name, openingDate.getOrElse(today)))
      case Failure(ex) => Failure(new Exception(s"开户失败 $no: $name", ex))
    }

  //消费出纳
  def debit(no: String, amount: Amount, repo: AccountRepository): Try[Account] =
    repo.query(no) match {
      case Success(Some(a)) =>
        if (a.quota.balance < amount) Failure(new Exception("余额不足"))
        else repo.save(a.copy(quota = Quota(a.quota.balance - amount)))
      case Success(None) => Failure(new Exception(s"账户 $no 不存在"))
      case Failure(ex) => Failure(new Exception(s"提款失败-卡号: $no 金额： $amount", ex))
    }
  //存款
  def credit(no: String, amount: Amount, repo: AccountRepository): Try[Account] =
    repo.query(no) match {
      case Success(Some(a)) => repo.save(a.copy(quota = Quota(a.quota.balance + amount)))
      case Success(None) => Failure(new Exception(s"账号 $no 不存在"))
      case Failure(ex) => Failure(new Exception(s"存款失败-卡号： $no 金额： $amount", ex))
    }

  //查询余额
  def balance(no: String, repo: AccountRepository): Try[Quota] = repo.quota(no)
}


/**
  * Lazy注入服务
  * @tparam Account
  * @tparam Amount
  * @tparam Quota
  */
trait AccountServiceLazy[Account, Amount, Quota] {
  def open(no: String, name: String, openingDate: Option[Date]): AccountRepository => Try[Account]
  def debit(no: String, amount: Amount): AccountRepository => Try[Account]
  def credit(no: String, amount: Amount): AccountRepository => Try[Account]
  def balance(no: String): AccountRepository => Try[Quota]

}

object AccountServiceLazy extends AccountServiceLazy[Account, Amount, Quota] {
  //开户
  def open(no: String, name: String, openingDate: Option[Date]) = (repo: AccountRepository) =>
    repo.query(no) match {
      case Success(Some(a)) => Failure(new Exception(s"用户 $no 已存在"))
      case Success(None) =>
        if (no.isEmpty || name.isEmpty) Failure(new Exception(s"名字不能为空") )
        else if (openingDate.getOrElse(today) before today) Failure(new Exception(s"开户时间非法"))
        else repo.save(Account(no, name, openingDate.getOrElse(today)))
      case Failure(ex) => Failure(new Exception(s"开户失败 $no: $name", ex))
    }

  //消费出纳
  def debit(no: String, amount: Amount) =  (repo: AccountRepository) =>
    repo.query(no) match {
      case Success(Some(a)) =>
        if (a.quota.balance < amount) Failure(new Exception("余额不足"))
        else repo.save(a.copy(quota = Quota(a.quota.balance - amount)))
      case Success(None) => Failure(new Exception(s"账户 $no 不存在"))
      case Failure(ex) => Failure(new Exception(s"提款失败-卡号: $no 金额： $amount", ex))
    }

  //存款
  def credit(no: String, amount: Amount) =  (repo: AccountRepository) =>
    repo.query(no) match {
      case Success(Some(a)) => repo.save(a.copy(quota = Quota(a.quota.balance + amount)))
      case Success(None) => Failure(new Exception(s"账号 $no 不存在"))
      case Failure(ex) => Failure(new Exception(s"存款失败-卡号： $no 金额： $amount", ex))
    }

  //查询余额
  def balance(no: String) = (repo: AccountRepository) => repo.quota(no)

}

/**
  * Monad 抽象
  */

import common._

//使用Reader Monad
trait AccountServiceMonad[Account, Amount, Quota] {
  def open(no: String, name: String, openingDate: Option[Date]): Reader[AccountRepository, Try[Account]]
  def debit(no: String, amount: Amount): Reader[AccountRepository, Try[Account]]
  def credit(no: String, amount: Amount): Reader[AccountRepository, Try[Account]]
  def balance(no: String): Reader[AccountRepository, Try[Quota]]
}
object AccountServiceMonad extends AccountServiceMonad[Account, Amount, Quota] {

  //开户
  def open(no: String, name: String, openingDate: Option[Date]) = Reader { (repo: AccountRepository) =>
    repo.query(no) match {
      case Success(Some(a)) => Failure(new Exception(s"用户 $no 已存在"))
      case Success(None) =>
        if (no.isEmpty || name.isEmpty) Failure(new Exception(s"名字不能为空") )
        else if (openingDate.getOrElse(today) before today) Failure(new Exception(s"开户时间非法"))
        else repo.save(Account(no, name, openingDate.getOrElse(today)))
      case Failure(ex) => Failure(new Exception(s"开户失败 $no: $name", ex))
    }
  }
  //消费出纳

  def debit(no: String, amount: Amount) = Reader { (repo: AccountRepository) =>
    repo.query(no) match {
      case Success(Some(a)) =>
        if (a.quota.balance < amount) Failure(new Exception("余额不足"))
        else repo.save(a.copy(quota = Quota(a.quota.balance - amount)))
      case Success(None) => Failure(new Exception(s"账户 $no 不存在"))
      case Failure(ex) => Failure(new Exception(s"提款失败-卡号: $no 金额： $amount", ex))
    }
  }

  //存款
  def credit(no: String, amount: Amount) = Reader { (repo: AccountRepository) =>
    repo.query(no) match {
      case Success(Some(a)) => repo.save(a.copy(quota = Quota(a.quota.balance + amount)))
      case Success(None) => Failure(new Exception(s"账号 $no 不存在"))
      case Failure(ex) => Failure(new Exception(s"存款失败-卡号： $no 金额： $amount", ex))
    }
  }


  //查询余额
  def balance(no: String) = Reader((repo: AccountRepository) => repo.quota(no))
}

/**
  * Kleisli允许我们组合返回值monadic值(比如Option Either Try)的函数（比如Reader,它就是一个接受AccountRepositoty，返回Try的函数）
  */

import cats.implicits._
trait AccountServiceKleisli[Account, Amount, Quota]{
  def open(no: String, name: String, openingDate: Option[Date]): Kleisli[Try,AccountRepository, Account]
  def debit(no: String, amount: Amount): Kleisli[Try,AccountRepository, Account]
  def credit(no: String, amount: Amount): Kleisli[Try,AccountRepository, Account]
  def balance(no: String): Kleisli[Try,AccountRepository, Quota]
}
object AccountServiceKleisli extends AccountServiceKleisli[Account, Amount, Quota]{

  override def open(no: String, name: String, openingDate: Option[Date]): Kleisli[Try,AccountRepository, Account] =
    Kleisli((repo: AccountRepository) =>
      repo.query(no) match {
        case Success(Some(a)) => Failure(new Exception(s"用户 $no 已存在"))
        case Success(None) =>
          if (no.isEmpty || name.isEmpty) Failure(new Exception(s"名字不能为空") )
          else if (openingDate.getOrElse(today) before today) Failure(new Exception(s"开户时间非法"))
          else repo.save(Account(no, name, openingDate.getOrElse(today)))
        case Failure(ex) => Failure(new Exception(s"开户失败 $no: $name", ex))
      })

  override def debit(no: String, amount: Amount): Kleisli[Try, AccountRepository, Account] =
    Kleisli((repo: AccountRepository) =>
      repo.query(no) match {
        case Success(Some(a)) =>
          if (a.quota.balance < amount) Failure(new Exception("余额不足"))
          else repo.save(a.copy(quota = Quota(a.quota.balance - amount)))
        case Success(None) => Failure(new Exception(s"账户 $no 不存在"))
        case Failure(ex) => Failure(new Exception(s"提款失败-卡号: $no 金额： $amount", ex))
      })

  override def credit(no: String, amount: Amount): Kleisli[Try, AccountRepository, Account] =
    Kleisli( (repo: AccountRepository) =>
      repo.query(no) match {
        case Success(Some(a)) => repo.save(a.copy(quota = Quota(a.quota.balance + amount)))
        case Success(None) => Failure(new Exception(s"账号 $no 不存在"))
        case Failure(ex) => Failure(new Exception(s"存款失败-卡号： $no 金额： $amount", ex))
      })

  override def balance(no: String): Kleisli[Try, AccountRepository, Quota] =
    Kleisli((repo: AccountRepository) => repo.quota(no))
}