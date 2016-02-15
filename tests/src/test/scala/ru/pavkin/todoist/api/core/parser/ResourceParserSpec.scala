package ru.pavkin.todoist.api.core.parser

import cats.Id
import org.scalacheck.Prop.forAll
import org.scalacheck.Properties
import org.scalatest.FlatSpec
import shapeless.test.illTyped
import shapeless.{::, HNil}

import scala.util.Try

trait ResourceParserTestData {
  val intParser = ResourceParser[Id, String, Int]((s: String) => Try(s.toInt).getOrElse(0))
  val doubleParser = ResourceParser[Id, String, Double]((s: String) => Try(s.toDouble).getOrElse(0.0))
  val intLengthParser = ResourceParser[Id, Int, Int]((s: Int) => s.toString.length)
  val identityParser = ResourceParser[Id, String, String]((s: String) => s)
}

class ResourceParserTest extends FlatSpec with ResourceParserTestData {

  "ResourceParser" should "work" in {
    implicit val p1 = intParser
    implicit val p2 = doubleParser

    implicitly[MultipleResourcesParser.Aux[Id, String, Double :: Int :: HNil]]
    implicitly[MultipleResourcesParser.Aux[Id, String, Int :: Double :: HNil]]
    implicitly[MultipleResourcesParser.Aux[Id, String, Int :: HNil]]

    illTyped("""implicitly[MultipleResourcesParser.Aux[Id, String, String :: Int :: HNil]]""")
  }

}

object ResourceParserSpec extends Properties("ResourceParser") with ResourceParserTestData {
  property("identity") = forAll { (a: String) =>
    identityParser.parse(a) == a
  }

  property("combination") = forAll { (a: String) =>
    intParser.combine(doubleParser).parse(a) == intParser.parse(a) :: doubleParser.parse(a) :: HNil
  }

  property("composition") = forAll { (a: String) =>
    intParser.compose(intLengthParser).parse(a) == intLengthParser.parse(intParser.parse(a))
  }
}
