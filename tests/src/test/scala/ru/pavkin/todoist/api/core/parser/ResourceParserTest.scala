package ru.pavkin.todoist.api.core.parser

import cats.Id
import org.scalatest.FunSuite
import org.scalatest.prop.Checkers
import shapeless.test.illTyped
import shapeless.{::, HNil}

import scala.util.Try

class ResourceParserTest extends FunSuite with Checkers {

  val intParser = ResourceParser[Id, String, Int]((s: String) => Try(s.toInt).getOrElse(0))
  val doubleParser = ResourceParser[Id, String, Double]((s: String) => Try(s.toDouble).getOrElse(0.0))
  val intLengthParser = ResourceParser[Id, Int, Int]((s: Int) => s.toString.length)
  val identityParser = ResourceParser[Id, String, String]((s: String) => s)

  test("ResourceParser") {
    implicit val p1 = intParser
    implicit val p2 = doubleParser

    implicitly[MultipleResourcesParser.Aux[Id, String, Double :: Int :: HNil]]
    implicitly[MultipleResourcesParser.Aux[Id, String, Int :: Double :: HNil]]
    implicitly[MultipleResourcesParser.Aux[Id, String, Int :: HNil]]

    illTyped("""implicitly[MultipleResourcesParser.Aux[Id, String, String :: Int :: HNil]]""")
  }

  test("ResourceParser identity") {
    check { (a: String) => identityParser.parse(a) == a }
  }

  test("ResourceParser combination") {
    check { (a: String) =>
      intParser.combine(doubleParser).parse(a) == intParser.parse(a) :: doubleParser.parse(a) :: HNil
    }
  }

  test("ResourceParser composition") {
    check { (a: String) =>
      intParser.compose(intLengthParser).parse(a) == intLengthParser.parse(intParser.parse(a))
    }
  }
}
