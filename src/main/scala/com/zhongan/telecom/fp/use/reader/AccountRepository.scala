package com.zhongan.telecom.fp.use.reader

import scala.util.{Failure, Success, Try}

class AccountRepository extends Repository [String,Account]{

  override def query(id: String): Try[Option[Account]] = ???

  override def save(aggr: Account): Try[Account] = ???

  def quota(id:String):Try[Quota] = query(id) match {
    case Success(Some(account)) => Success(account.quota)
    case Success(None) => Failure(new Exception(s"没有 $id 对应的而账户存在"))
    case Failure(exception) => Failure(exception)
  }
}
