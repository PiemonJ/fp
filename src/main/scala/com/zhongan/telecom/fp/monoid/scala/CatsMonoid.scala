package com.zhongan.telecom.fp.monoid.scala

object CatsMonoid {

  def main(args: Array[String]): Unit = {
    import cats.Monoid
    import cats.instances.int._

    val intAddition = Monoid[Int]

    val result = intAddition.combine(10,20)

    println(result)

    import cats.instances.option._
    val optionIntMonoid = Monoid[Option[Int]]
    val op = optionIntMonoid.combine(Some(10),Some(10))
    println(op.get)



  }

}
