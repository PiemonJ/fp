package com.zhongan.telecom.fp.use

import java.util.Calendar

import com.zhongan.telecom.fp.use.reader._
import com.zhongan.telecom.fp.use.reader.common.Amount

import scala.util.{Failure, Success, Try}

object Demo {

  def main(args: Array[String]): Unit = {


    //转账模拟
    //定义计算：Define
//    val tranfer =
//        AccountServiceMonad.credit("123",BigDecimal(100))
//          .flatMap(c1 => {
//            AccountServiceMonad.credit("123",BigDecimal(200))
//              .flatMap(
//                c2 => {
//                  AccountServiceMonad.debit("123",BigDecimal(250))
//                    .flatMap(
//                      d1 => AccountServiceMonad.balance("123")
//                    )
//                }
//              )
//          });
//
//        //执行计算:execute
//        tranfer run AccountRepositoryInMemory



    AccountService.open("123","zhangsan",Some(Calendar.getInstance().getTime()),AccountRepositoryInMemory)

//    transfer("123")
//
//    val balance = transferViaReaderMonad("123") run AccountRepositoryInMemory

    val balance = transferViaKleisliMonad("123") run AccountRepositoryInMemory

    val amount = balance match {
      case Success(value) => value
      case _ => Nil
    }

    print(amount)



  }

  def transfer(no: String) = for {
    _ <- AccountService.credit(no, BigDecimal(100),AccountRepositoryInMemory)
    _ <- AccountService.credit(no, BigDecimal(300),AccountRepositoryInMemory)
    _ <- AccountService.debit(no, BigDecimal(160),AccountRepositoryInMemory)
    b <- AccountService.balance(no,AccountRepositoryInMemory)
  } yield b

  def transferLazy(no: String) = for{

  }

  def transferViaReaderMonad(no: String) = for {
    _ <- AccountServiceMonad.credit(no, BigDecimal(100))
    _ <- AccountServiceMonad.credit(no, BigDecimal(300))
    _ <- AccountServiceMonad.debit(no, BigDecimal(160))
    b <- AccountServiceMonad.balance(no)
  } yield b

  import cats.implicits._
  def transferViaKleisliMonad(no: String) = for {

    _ <- AccountServiceKleisli.credit(no, BigDecimal(100))
    _ <- AccountServiceKleisli.credit(no, BigDecimal(300))
    _ <- AccountServiceKleisli.debit(no, BigDecimal(160))
    b <- AccountServiceKleisli.balance(no)
  } yield b
}
