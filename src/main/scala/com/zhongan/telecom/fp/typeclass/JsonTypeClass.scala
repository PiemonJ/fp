package com.zhongan.telecom.fp.typeclass

import com.zhongan.telecom.fp.typeclass.chap1_1.Person

trait JsonWriterTypeClass[A] {

  def write(value :A) : Json
}
object JsonWriterTypeClassInstances{

  implicit val stringJsonWriter = new JsonWriterTypeClass[String] {
    def write(value: String): Json =
      JsString(value)
  }

  implicit val numberJsonWriter = new JsonWriterTypeClass[Double] {
    def write(value: Double): Json = JsNumber(value)
  }

  implicit val personJsonWriter = new JsonWriterTypeClass[Person] {
    def write(value: Person): Json = JsObject(
      Map(
        "name" -> JsString(value.email),
        "email" -> JsString(value.email),
      )
    )
  }


}
