package com.zhongan.telecom.fp.laziness

import scala.annotation.tailrec

sealed trait Stream[+A]{




  def toList:List[A] = this match {
    case Empty => Nil
    case Cons(head, tail) => head() :: (tail().toList)
  }

  def toListRec:List[A] = {
    @tailrec
    def go(stream:Stream[A],acc:List[A]):List[A] = {
      stream match {
        case Empty => Nil
        case Cons(head, tail) => go(tail(),head() :: acc)
      }
    }
    go(this,List[A]())
  }

  def toListFast:List[A] = {
    val buf = new collection.mutable.ListBuffer[A]

    def go(stream: Stream[A]):List[A]= {
      stream match {
        case Empty => buf.toList
        case Cons(head, tail) => {
          buf += head()
          go(tail())
        }
      }
    }

    go(this)
  }


  def take(n:Int) :Stream[A] = this match {
    case Empty => Empty
    case Cons(head, tail) => Stream.cons(head(),tail().take(n - 1))
  }

  def takeViaUnfold(n: Int): Stream[A] = Stream.unfold((this,n)){

    case (Cons(head,tail),n) if n > 0 => Some(head(),(tail(),n - 1))
    case _ => None
  }

  def drop(n:Int):Stream[A] = this match {
    case Cons(head, tail) if n > 0 => tail().drop(n - 1)
    case _ => this
  }

  def takeWhile(when:A => Boolean):Stream[A] = this match {
    case Cons(head, tail) if head() == true => Stream.cons(head(),tail().takeWhile(when))
    case _ => this
  }

  def exist(when: A => Boolean):Boolean = this match {
    case Cons(head, tail) => {println("head:" + head());when(head())} || tail().exist(when)
    case _ => false
  }


  def foldRight[B](zero: => B)(func: (A ,=> B) => B):B = this match {
    case Cons(head, tail) => func(head(),tail().foldRight(zero)(func))

    case _ => zero
  }


  def existViaFold(when:A => Boolean):Boolean = foldRight(false)((ele,zero) => when(ele) || zero)

  def forAllViaFold(when:A => Boolean):Boolean = foldRight(true)((ele,zero) => when(ele) && zero)

  def headOptionViaFold:Option[A] = foldRight(None:Option[A])((ele,_) => Some(ele))

  def mapViaFold[B](func:A => B):Stream[B] = foldRight(Empty:Stream[B])((ele,zero) => Stream.cons(func(ele),zero))

  def filterViaFold(func:A => Boolean):Stream[A] = foldRight(Empty:Stream[A]){
    (ele,zero) => if(func(ele)) Stream.cons(ele,zero) else zero
  }


  def appendViaFold[B>:A](s: => Stream[B]): Stream[B] = foldRight(s)((ele,zero) => Stream.cons(ele,zero))

  def flatMapViaFold[B](func : A => Stream[B]) = foldRight(Empty:Stream[B])((ele,zero) => func(ele).appendViaFold(zero))

}

case object Empty extends Stream[Nothing]

case class Cons[+A](head:() => A,tail:() => Stream[A]) extends Stream[A]

object Stream{

  //智能构造器
  def empty[A]:Stream[A] = Empty

  def cons[A](head: => A,tail: => Stream[A]) = {
    lazy val h = head
    lazy val t = tail
    Cons(() => h,() => t)
  }


  def apply[A](as:A*): Stream[A] = {
    if (as.isEmpty)
      empty
    else cons(as.head,apply(as.tail:_*))

  }


  def constant[A](a:A):Stream[A] = cons(a,constant(a))

  def from(n:Int):Stream[Int] = cons(n,from(n + 1))

  def unfold[A,S](z:S)(f:S => Option[(A,S)]):Stream[A] = f(z) match {
    case None => Empty
    case Some((a,s)) => cons(a,unfold(s)(f))
  }


  def constantViaUnfold[A](unit:A):Stream[A] = unfold(unit)(u => Some(u,u))

  def fromViaUnfold(unit:Int):Stream[Int] = unfold(unit)(unit => Some((unit,unit + 1)))



  def main(args: Array[String]): Unit = {
    val ss = Stream(1,2,3,4,5).takeViaUnfold(2);

    println(ss.toList)
  }
}
