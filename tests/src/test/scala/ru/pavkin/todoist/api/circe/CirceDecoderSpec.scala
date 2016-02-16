package ru.pavkin.todoist.api.circe

import cats.data.Xor
import io.circe.Json
import org.scalatest.FunSuite
import org.scalatest.prop.Checkers
import io.circe.parser._
import io.circe.generic.auto._

class CirceDecoderSpec extends FunSuite with Checkers {

  val fixture = parse(
    """{
      "a": 123,
      "b": {
        "c": "str"
      }
    }""").getOrElse(Json.empty)

  case class A(a: Int)
  case class B(c: String)

  test("CirceDecoder uses locator correctly") {
    val aDecoder = new CirceDecoder[A](Option(_))
    val bDecoder = new CirceDecoder[B](_.asObject.flatMap(_ ("b")))

    aDecoder.parse(fixture) == Xor.Right(A(123)) &&
      bDecoder.parse(fixture) == Xor.Right(B("str"))
  }

  test("CirceDecoder fails if locator fails") {
    val aDecoder = new CirceDecoder[A](_.asObject.flatMap(_ ("c")))
    aDecoder.parse(fixture).isLeft
  }

  test("CirceDecoder fails if decoder fails") {
    val aDecoder = new CirceDecoder[B](_.asObject.flatMap(_ ("a")))
    aDecoder.parse(fixture).isLeft
  }
}

