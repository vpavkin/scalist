package ru.pavkin.todoist.api.core.decoder

import cats.Id
import org.scalatest.FunSuite
import org.scalatest.prop.Checkers
import shapeless.test.illTyped
import shapeless.{::, HNil}

import scala.util.Try

class CommandResponseDecoderSpec extends FunSuite with Checkers {

  val stingLengthParser = SingleCommandResponseDecoder.using[Id, Int, String, Boolean] {
    (c: Int, s: String) => Try(s.length == c).getOrElse(false)
  }
  val toLongParser = SingleCommandResponseDecoder.using[Id, Double, String, Long] {
    (c: Double, s: String) => Try(c.ceil.toLong).getOrElse(0L)
  }
  val identityCommandParser = SingleCommandResponseDecoder.using[Id, Int, String, String] {
    (c: Int, s: String) => c.toString + s
  }
  val longEqualsToDoubleParser = SingleCommandResponseDecoder.using[Id, Double, Long, Boolean] {
    (c: Double, s: Long) => c.ceil.toLong == s
  }

  test("CommandResponseDecoder") {
    implicit val p1 = stingLengthParser
    implicit val p2 = toLongParser

    implicitly[MultipleCommandResponseDecoder.Aux[Id, Double :: Int :: HNil, String, Long :: Boolean :: HNil]]
    implicitly[MultipleCommandResponseDecoder.Aux[Id, Int :: Double :: HNil, String, Boolean :: Long :: HNil]]
    implicitly[MultipleCommandResponseDecoder.Aux[Id, Int :: HNil, String, Boolean :: HNil]]

    illTyped("""implicitly[SingleCommandResponseDecoder[Id, Boolean, String]]""")
    illTyped("""implicitly[MultipleCommandResponseDecoder[Id, String :: HNil, String]]""")
    illTyped("""implicitly[MultipleCommandResponseDecoder[Id, Double :: String :: HNil, String]]""")
    illTyped("""implicitly[MultipleCommandResponseDecoder[Id, String :: Double :: HNil, String]]""")
  }

  test("CommandResponseDecoder identity") {
    check { (i: Int, a: String) => identityCommandParser.parse(i)(a) == i.toString + a }
  }

  test("CommandResponseDecoder combination") {
    check { (i: Int, d: Double, r: String) =>
      stingLengthParser
        .combine(toLongParser)
        .parse(d :: i :: HNil)(r) ==
        toLongParser.parse(d)(r) :: stingLengthParser.parse(i)(r) :: HNil
    }
  }

  test("CommandResponseDecoder composition") {
    check { (i: Double, r: String) =>
      toLongParser.compose(longEqualsToDoubleParser).parse(i)(r) ==
        longEqualsToDoubleParser.parse(i)(toLongParser.parse(i)(r))
    }
  }
}
