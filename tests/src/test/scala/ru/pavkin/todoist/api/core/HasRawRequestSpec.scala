package ru.pavkin.todoist.api.core

import org.scalacheck.Gen
import org.scalatest.{Matchers, FunSuite}
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import ru.pavkin.todoist.api.RawRequest
import shapeless.test.illTyped
import shapeless.{::, HNil}

class HasRawRequestSpec extends FunSuite with Matchers with GeneratorDrivenPropertyChecks {

  test("HasRawRequest") {
    implicit val i1 = HasRawRequest[Int](Vector("Int"))
    implicit val i2 = HasRawRequest[String](Vector("String"))

    HasRawRequest[Int]
    HasRawRequest[String]
    HasRawRequest[Int :: String :: HNil]

    illTyped("""HasRawRequest[Boolean]""")
    illTyped("""HasRawRequest[Boolean :: Int :: HNil]""")
    illTyped("""HasRawRequest[Int :: Boolean :: HNil]""")
  }

  implicit val strGen: Gen[String] = Gen.alphaStr

  test("HasRawRequest combinates") {
    forAll((a: RawRequest, b: RawRequest) => {
      implicit val i1 = HasRawRequest[Int](a)
      implicit val i2 = HasRawRequest[String](b)
      HasRawRequest[Int :: String :: HNil].rawRequest shouldEqual a ++ b
    })
  }
}

