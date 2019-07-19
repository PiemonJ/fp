package com.zhongan.telecom.fp.typeclass

object chap1_1 {

  /**
    * Type Class 剖析
    *
    * TypeClass模式有三个重要组件：
    *   1.Type Class本身
    *   2.特定类型的Type Class实例
    *   3.我们暴露给用户的接口方法
    *
    * 1.1 Type Class
    * Type Class是一个接口或API，它表示我们希望实现的一些功能。在Cats中，Type Class由至少包含一个类型参数的特质表示。例如，我们可以表示一般的“序列化为JSON”行为如下:
    */

  // Define a very simple JSON AST
  sealed trait Json
  final case class JsObject(get: Map[String, Json]) extends Json
  final case class JsString(get: String) extends Json
  final case class JsNumber(get: Double) extends Json
  case object JsNull extends Json
  // The "serialize to JSON" behaviour is encoded in this trait
  trait JsonWriter[A] {
    def write(value: A): Json
  }

  /**
    * JsonWriter即是我们的Type Class，使用Json及其子类型提供支持代码
    */

  /**
    * 1.2 Type Class Instances
    * Type Class的实例为我们关注的类型提供了实现，包括来自Scala标准库的类型和来自领域模型中的类型。
    *
    * 在Scala中，我们通过创建Type Class的具体实现来定义实例，并使用implicit关键字标记它们:
    *
    *
    */

  final case class Person(name: String, email: String)
  object JsonWriterInstances {
    implicit val stringWriter: JsonWriter[String] =
      new JsonWriter[String] {
        def write(value: String): Json =
          JsString(value)
      }
    implicit val personWriter: JsonWriter[Person] =
      new JsonWriter[Person] {
        def write(value: Person): Json =
          JsObject(Map(
            "name" -> JsString(value.name),
            "email" -> JsString(value.email)
          ))
      }
    // etc...
  }

  /**
    * 1.3 Type Class Interfaces
    *  type class接口是我们向用户公开的任何功能。接口是接受type class的实例作为隐式参数的通用方法。
    * 有两种指定接口的常见方法:: Interface Objects和Interface Syntax。
    *
    * 1.3.1 Interface Objects
    * 创建接口最简单的方法是将方法放在一个单例对象中:
    *
    *
    */

//  object Json {
//    def toJson[A](value: A)(implicit w: JsonWriter[A]): Json =
//      w.write(value)
//  }

  /**
    * 要使用这个对象，我们导入任何我们关注的type class实例，并调用相关的方法:
    */

//  import JsonWriterInstances._
//  Json.toJson(Person("Dave", "dave@example.com"))
  // res4: Json = JsObject(Map(name -> JsString(Dave), email -> JsString (dave@example.com)))

  /**
    * 编译器发现我们在不提供隐式参数的情况下调用了toJson方法。它试图通过搜索相关类型的类型类实例并将它们插入调用站点来解决这个问题:
    */

//  Json.toJson(Person("Dave", "dave@example.com"))(personWriter)

  /**
    *
    * 1.3.2 Interface Syntax
    *
    * 我们也可以选择使用扩展方法来用接口方法扩展现有的类型。cat将这称为type类的“语法”:
    */

  object JsonSyntax {
    implicit class JsonWriterOps[A](value: A) {
      def toJson(implicit w: JsonWriter[A]): Json =
        w.write(value)
    }
  }

  /**
    *
    * 我们通过将Interface Syntax与实例一起导入我们需要的类型来使用接口语法：
    *
    * 同样，编译器会搜索隐式参数的候选项，并为我们填充它们:
    */

  import JsonSyntax._
  import JsonWriterInstances._


  case class Country(country:String)


  Person("Dave", "dave@example.com").toJson
  // res6: Json = JsObject(Map(name -> JsString(Dave), email -> JsString (dave@example.com)))

  Person("Dave", "dave@example.com").toJson(personWriter)

  /**
    *
    * 1.3.3  implicitly方法
    * Scala标准库提供了一个称为implicitly的通用 type class
    *
    *
    */

//  def implicitly[A](implicit value: A): A =
//    value

  /**
    * 我们可以通过implicitly，调用隐式作用域中的任何值。我们提供我们想要的类型，并隐式地完成其余的工作
    */

  import JsonWriterInstances._
  implicitly[JsonWriter[String]]
  // res8: JsonWriter[String] = JsonWriterInstances$$anon$1@73eb1c7a

  /**
    *
    * cat中的大多数类型类都提供了调用实例的其他方法。但是，对于调试目的来说，隐式是一个很好的后备方法。
    * 我们可以在代码的常规流中隐式地插入一个调用to，以确保编译器能够找到类型类的实例，并确保没有模糊的隐式错误。
    */


}
