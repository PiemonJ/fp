package com.zhongan.telecom.fp.functor

trait Functor[F[_]] {

  def map[A,B](fa:F[A])(func: A => B):F[B]

  def unzip[A,B](fab:F[(A,B)]):(F[A],F[B]) = {
    (map(fab)(_._1),map(fab)(_._2))
  }



}

object Functor{


  //List：类型构造器
  //List是一个函子
  val listFunctor = new Functor[List] {
    override def map[A, B](fa: List[A])(func: A => B): List[B] = fa map func
  }

}
