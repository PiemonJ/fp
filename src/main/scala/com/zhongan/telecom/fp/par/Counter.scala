package com.zhongan.telecom.fp.par

object Counter {

  def normalSum(ints:IndexedSeq[Int]):Int = ints.foldLeft(0)(_ + _)

  //分支递归计算总和
  def sum(ints:IndexedSeq[Int]):Int = {
    if (ints.size <= 1 ){
      ints.headOption.getOrElse(0)  //等同于Java的Optional的orElse
    } else {
      val (l,r) = ints.splitAt(ints.length / 2)
      sum(l) + sum(r)
    }
  }

}
