package ru.pavkin.todoist.api.core.parser

import cats.Id
import org.scalatest.FunSuite
import org.scalatest.prop.Checkers
import shapeless.test.illTyped
import shapeless.{::, HNil}

import scala.util.Try

class ResponseDecoderSpec extends FunSuite with Checkers {

  val intParser = SingleResponseDecoder.using[Id, String, Int]((s: String) => Try(s.toInt).getOrElse(0))
  val doubleParser = SingleResponseDecoder.using[Id, String, Double]((s: String) => Try(s.toDouble).getOrElse(0.0))
  val intLengthParser = SingleResponseDecoder.using[Id, Int, Int]((s: Int) => s.toString.length)
  val identityParser = SingleResponseDecoder.using[Id, String, String]((s: String) => s)

  test("ResponseDecoder") {
    implicit val p1 = intParser
    implicit val p2 = doubleParser

    implicitly[MultipleResponseDecoder.Aux[Id, String, Double :: Int :: HNil]]
    implicitly[MultipleResponseDecoder.Aux[Id, String, Int :: Double :: HNil]]
    implicitly[MultipleResponseDecoder.Aux[Id, String, Int :: HNil]]

    illTyped("""implicitly[MultipleResponseDecoder.Aux[Id, String, String :: Int :: HNil]]""")
  }

  test("ResponseDecoder identity") {
    check { (a: String) => identityParser.parse(a) == a }
  }

  test("ResponseDecoder combination") {
    check { (a: String) =>
      intParser.combine(doubleParser).parse(a) == intParser.parse(a) :: doubleParser.parse(a) :: HNil
    }
  }

  test("ResponseDecoder composition") {
    check { (a: String) =>
      intParser.compose(intLengthParser).parse(a) == intLengthParser.parse(intParser.parse(a))
    }
  }
}
