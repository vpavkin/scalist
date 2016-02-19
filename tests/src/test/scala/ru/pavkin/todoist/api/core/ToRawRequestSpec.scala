package ru.pavkin.todoist.api.core

import org.scalacheck.Gen.alphaStr
import org.scalacheck.Gen.posNum
import org.scalatest.{Matchers, FunSuite}
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import shapeless.test.illTyped
import shapeless.{::, HNil}

class ToRawRequestSpec extends FunSuite with Matchers with GeneratorDrivenPropertyChecks {

  implicit val i1 = ToRawRequest.command[Int]((i: Int) => List(i.toString))
  implicit val i2 = ToRawRequest.command[String]((s: String) => List(s))

  test("ToRawRequest") {

    ToRawRequest[Int]
    ToRawRequest[String]
    ToRawRequest[Int :: String :: HNil].rawRequest(2 :: "abc" :: HNil) shouldBe
      Map("commands" -> List("2", "abc"))

    illTyped("""ToRawRequest[Boolean]""")
    illTyped("""ToRawRequest[Boolean :: Int :: HNil]""")
    illTyped("""ToRawRequest[Int :: Boolean :: HNil]""")
  }

  test("ToRawRequest combinates") {
    forAll(posNum[Int], alphaStr) { (a: Int, b: String) =>
      ToRawRequest[Int :: String :: HNil].rawRequest(a :: b :: HNil) shouldEqual
        Map(ToRawRequest.COMMANDS -> List(a.toString, b))
    }
  }
}

