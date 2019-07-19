package com.zhongan.telecom.fp.panic.scala

import scala.util.{Failure, Success, Try}

object ScalaPanic {

  def tryDiv(x:Int,y:Int):Try[Int] = {
    Try(x / y)
  }

  def safeDiv(x:Int,y:Int):Either[Throwable,Int] = {

    tryDiv(x,y) match {
      case Success(value) => Right(value)
      case Failure(ex) => Left(ex)
    }
  }

}
