package com.zhongan.telecom.fp.typeclass

sealed trait Json

final case class JsObject(get: Map[String, Json]) extends Json
final case class JsString(get: String) extends Json
final case class JsNumber(get: Double) extends Json
case object JsNull extends Json

object Json{

  //方法1:Interface Object
  def toJson[A](value : A)(implicit writer :JsonWriterTypeClass[A]): Json ={
    writer.write(value)
  }

}

object JsonSyntax{


  /**
    *
    * 方法二：Interface Syntax
    * PS:1.只能在trait/class/object内定义隐式类
    *    2.构造函数只能携带一个非隐式参数，如下，我们只能有一个value,不可以在有其他的非隐式参数
    *     implicit class RichDate(date: java.util.Date) // 正确！
    *     implicit class Indexer[T](collecton: Seq[T], index: Int) // 错误！
    *     implicit class Indexer[T](collecton: Seq[T])(implicit index: Index) // 正确！
    *    3.作用域内不能有重名的方法、成员或对象
    *
    * @param value
    * @tparam A
    */
  implicit class JsonTypeSyntax[A](value : A){
    def toJson(implicit w: JsonWriterTypeClass[A]): Json =
      w.write(value)
  }
}

