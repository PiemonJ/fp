package com.zhongan.telecom.fp.par

class Par[A]{

}

object Par{

  def unit[A](a : => A):Par[A] = ???

  def get[A](par:Par[A]):A = ???

  def sum(ints:IndexedSeq[Int]):Int = {
    if (ints.size <= 1){
      ints.headOption getOrElse 0
    } else {
      Par.
    }
  }

}