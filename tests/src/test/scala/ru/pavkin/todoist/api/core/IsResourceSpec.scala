package ru.pavkin.todoist.api.core

import org.scalatest.FunSuite
import org.scalatest.prop.Checkers
import ru.pavkin.todoist.api.RawRequest
import shapeless.test.illTyped
import shapeless.{::, HNil}

class IsResourceSpec extends FunSuite with Checkers {

  test("IsResource") {
    implicit val i1 = IsResource[Int](Vector("Int"))
    implicit val i2 = IsResource[String](Vector("String"))

    IsResource[Int]
    IsResource[String]
    IsResource[Int :: String :: HNil]

    illTyped("""IsResource[Boolean]""")
    illTyped("""IsResource[Boolean :: Int :: HNil]""")
    illTyped("""IsResource[Int :: Boolean :: HNil]""")
  }

  test("IsResource combinates") {
    check { (a: RawRequest, b: RawRequest) =>
      implicit val i1 = IsResource[Int](a)
      implicit val i2 = IsResource[String](b)
      IsResource[Int :: String :: HNil].strings == a ++ b
    }
  }
}

