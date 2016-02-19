package ru.pavkin.todoist.api.core

import org.scalatest.FunSuite
import org.scalatest.prop.Checkers
import shapeless.test.illTyped
import shapeless.{::, HNil}

class ToRawRequestSpec extends FunSuite with Checkers {

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
    check { (a: Int, b: String) =>
      ToRawRequest[Int :: String :: HNil].rawRequest(a :: b :: HNil) == Vector(a.toString, b)
    }
  }
}

