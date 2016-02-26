package ru.pavkin.todoist.api.core.decoder

import cats.Id
import org.scalacheck.Gen
import org.scalatest.{Matchers, FunSuite}
import org.scalatest.prop.{GeneratorDrivenPropertyChecks, Checkers}
import shapeless.test.illTyped
import shapeless.{::, HNil}

import scala.util.Try

class ResponseDecoderSpec extends FunSuite with Checkers with Matchers with GeneratorDrivenPropertyChecks{

  case class Smth(n: Int)
  val intParser = SingleResponseDecoder.using[Id, String, Int]((s: String) => Try(s.toInt).getOrElse(0))
  val doubleParser = SingleResponseDecoder.using[Id, String, Double]((s: String) => Try(s.toDouble).getOrElse(0.0))
  val intLengthParser = SingleResponseDecoder.using[Id, Int, Long]((s: Int) => s.toString.length.toLong)
  val identityParser = SingleResponseDecoder.using[Id, Boolean, Boolean]((s: Boolean) => s)
  val smthParser = SingleResponseDecoder.using[Id, Int, Smth]((n: Int) => Smth(n))

  val smthCommandDecoder = SingleCommandResponseDecoder.using[Id, Smth, Smth, Boolean] {
    (smth: Smth, n: Smth) => smth.n == n.n
  }
  val smthStringLengthDecoder = SingleCommandResponseDecoder.using[Id, String, Smth, String] {
    (command: String, base: Smth) => (base.n + command.length).toString
  }

  test("ResponseDecoder") {
    implicit val p1 = intParser
    implicit val p2 = doubleParser

    implicitly[MultipleResponseDecoder.Aux[Id, String, Double :: Int :: HNil]]
    implicitly[MultipleResponseDecoder.Aux[Id, String, Int :: Double :: HNil]]
    implicitly[MultipleResponseDecoder.Aux[Id, String, Int :: HNil]]

    illTyped("""implicitly[MultipleResponseDecoder.Aux[Id, String, String :: Int :: HNil]]""")
  }

  test("ResponseDecoder identity") {
    check { (a: Boolean) => identityParser.parse(a) == a }
  }

  test("ResponseDecoder combination") {
    check { (a: String) =>
      intParser.combine(doubleParser).parse(a) == intParser.parse(a) :: doubleParser.parse(a) :: HNil
    }
  }

  test("ResponseDecoder composition") {
    forAll(Gen.alphaStr) { (a: String) =>
      intParser.compose(intLengthParser).parse(a) shouldBe intLengthParser.parse(intParser.parse(a))
    }
  }

  test("ResponseDecoder composition with multiple") {
    check { (a: String) =>
      intParser.compose(intLengthParser.combine(smthParser)).parse(a) ==
        intLengthParser.combine(smthParser).parse(intParser.parse(a))
    }
  }

  test("ResponseDecoder composition with single command decoder") {
    check { (s: Int, a: Int) =>
      smthParser.compose(smthCommandDecoder).parse(Smth(s))(a) == (s == a)
    }
  }

  test("ResponseDecoder composition with multiple command decoder") {
    check { (c1: Int, c2: String, base: Int) =>
      smthParser.compose(
        smthCommandDecoder.combine(smthStringLengthDecoder)
      ).parse(c2 :: Smth(c1) :: HNil)(base) == {
        val nBase = smthParser.parse(base)
        smthStringLengthDecoder.parse(c2)(nBase) :: smthCommandDecoder.parse(Smth(c1))(nBase) :: HNil
      }
    }
  }
}
