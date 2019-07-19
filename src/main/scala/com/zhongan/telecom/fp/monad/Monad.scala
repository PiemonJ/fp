package com.zhongan.telecom.fp.monad

import com.zhongan.telecom.fp.functor.Functor

trait Monad[F[_]] extends Functor[F[_]]{

  def unit[A](a : => A):F[A]

  def flatMap[A,B](ma:F[A])(func:A => F[B]):F[B]

  def map[A,B](ma:F[A])(func:A => B):F[B] = flatMap(ma)(a => unit(func(a)))

  def map2[A,B,C](ma:F[A],mb:F[B])(func:(A,B) => C):F[C] = flatMap(ma)(a => map(mb)(b => func(a,b)))

  def sequence[A](lma:List[F[A]]):F[List[A]] = lma.foldRight(unit(List[A]()))((ele,zero) => map2(ele,zero)(_ :: _))

  def traverse[A,B](la:List[A])(func:A => F[B]):F[List[B]] = la.foldRight(unit(List[B]()))((ele,zero) => map2(func(ele),zero)(_ :: _))

}
