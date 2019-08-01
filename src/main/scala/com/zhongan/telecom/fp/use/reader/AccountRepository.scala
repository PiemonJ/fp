package com.zhongan.telecom.fp.use.reader

import scala.util.{Failure, Success, Try}

trait AccountRepository extends Repository [String,Account]{

  def query(id: String): Try[Option[Account]]

  def save(aggr: Account): Try[Account]

  def quota(id:String):Try[Quota] = query(id) match {
    case Success(Some(account)) => Success(account.quota)
    case Success(None) => Failure(new Exception(s"没有 $id 对应的而账户存在"))
    case Failure(exception) => Failure(exception)
  }
}

//Mongo Version
object AccountRepositoryMongo extends AccountRepository{

  override def query(id: String): Try[Option[Account]] = Failure(new Exception("模拟"))

  override def save(aggr: Account): Try[Account] = Failure(new Exception("模拟"))
}
//Mysql Version
object AccountRepositoryMysql extends AccountRepository{

  override def query(id: String): Try[Option[Account]] = Failure(new Exception("模拟"))

  override def save(aggr: Account): Try[Account] = Failure(new Exception("模拟"))
}

import java.util.Date
import util.{ Try, Success, Failure }
import collection.mutable.{ Map => MMap }

object AccountRepositoryInMemory extends AccountRepository {
  lazy val repo = MMap.empty[String, Account]

  def query(no: String): Try[Option[Account]] = Success(repo.get(no))
  def save(a: Account): Try[Account] = {
    val r = repo += ((a.no, a))
    Success(a)
  }
  def query(openedOn: Date): Try[Seq[Account]] = Success(repo.values.filter(_.openDate == openedOn).toSeq)
}

