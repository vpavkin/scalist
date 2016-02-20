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
  case class D(b: B)

  test("CirceDecoder works if decoder works") {
    val aDecoder = new CirceDecoder[A]
    val bDecoder = new CirceDecoder[D]

    aDecoder.parse(fixture) == Xor.Right(A(123)) &&
      bDecoder.parse(fixture) == Xor.Right(D(B("str")))
  }

  test("CirceDecoder fails if decoder fails") {
    val aDecoder = new CirceDecoder[B]
    aDecoder.parse(fixture).isLeft
  }
}

