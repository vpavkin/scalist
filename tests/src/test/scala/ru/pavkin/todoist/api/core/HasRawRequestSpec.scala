package ru.pavkin.todoist.api.core

import org.scalacheck.Gen
import org.scalatest.{Matchers, FunSuite}
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import shapeless.test.illTyped
import shapeless.{::, HNil}

class HasRawRequestSpec extends FunSuite with Matchers with GeneratorDrivenPropertyChecks {

  test("HasRawRequest") {
    implicit val i1 = HasRawRequest.resource[Int](List("Int"))
    implicit val i2 = HasRawRequest.resource[String](List("String"))

    HasRawRequest[Int]
    HasRawRequest[String]
    HasRawRequest[Int :: String :: HNil].rawRequest shouldBe Map("resource_types" -> List("\"Int\"", "\"String\""))

    illTyped("""HasRawRequest[Boolean]""")
    illTyped("""HasRawRequest[Boolean :: Int :: HNil]""")
    illTyped("""HasRawRequest[Int :: Boolean :: HNil]""")
  }

  implicit val strGen: Gen[String] = Gen.alphaStr

  test("HasRawRequest combinates") {
    forAll((k: String, v1: String, v2: String) => {
      implicit val i1 = HasRawRequest[Int](Map(k -> List(v1)))
      implicit val i2 = HasRawRequest[String](Map(k -> List(v2)))
      HasRawRequest[Int :: String :: HNil].rawRequest shouldEqual Map(k -> List(v1, v2))
    })
  }
}

