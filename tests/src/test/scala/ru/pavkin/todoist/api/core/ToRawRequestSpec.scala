package ru.pavkin.todoist.api.core

import org.scalacheck.Gen.alphaStr
import org.scalacheck.Gen.posNum
import org.scalatest.{Matchers, FunSuite}
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import shapeless.test.illTyped
import shapeless.{::, HNil}

class ToRawRequestSpec extends FunSuite with Matchers with GeneratorDrivenPropertyChecks {

  implicit val i1 = ToRawRequest[Int]((i: Int) => Vector(i.toString))
  implicit val i2 = ToRawRequest[String]((s: String) => Vector(s))

  test("ToRawRequest") {

    ToRawRequest[Int]
    ToRawRequest[String]
    ToRawRequest[Int :: String :: HNil]

    illTyped("""ToRawRequest[Boolean]""")
    illTyped("""ToRawRequest[Boolean :: Int :: HNil]""")
    illTyped("""ToRawRequest[Int :: Boolean :: HNil]""")
  }

  test("ToRawRequest combinates") {
    forAll(posNum[Int], alphaStr) { (a: Int, b: String) =>
      ToRawRequest[Int :: String :: HNil].rawRequest(a :: b :: HNil) shouldEqual Vector(a.toString, b)
    }
  }
}

