package ru.pavkin.todoist.api.circe

import cats.data.Xor
import io.circe.parser._
import io.circe.generic.auto._
import io.circe.{Decoder, Json}
import org.scalatest.{FunSuite, Matchers}
import ru.pavkin.todoist.api.circe.decoders.PlainCoproductDecoder
import shapeless.{Inr, :+:, CNil, Inl}

class PlainCoproductDecoderSpec extends FunSuite with Matchers with PlainCoproductDecoder {

  def json(str: String) = parse(str).getOrElse(Json.empty)

  case class A(a: Int)
  case class B(b: String)

  type AB = A :+: B :+: CNil

  val decoder = Decoder[AB]

  test("PlainCoproductDecoder decodes valid json") {
    decoder.decodeJson(json("""{ "a" : 12 }""")) shouldBe Xor.Right(Inl(A(12)))
    decoder.decodeJson(json("""{ "b" : "str" }""")) shouldBe Xor.Right(Inr(Inl(B("str"))))
  }

  test("PlainCoproductDecoder decodes tail value first") {
    decoder.decodeJson(json("""{ "a" : 12, "b" : "str" }""")) shouldBe Xor.Right(Inr(Inl(B("str"))))
  }

  test("PlainCoproductDecoder fails if no suitable json found") {
    decoder.decodeJson(json("""{}""")).isLeft shouldBe true
  }
}

