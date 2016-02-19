package ru.pavkin.todoist.api.core

import org.scalatest.FunSuite
import org.scalatest.prop.Checkers
import ru.pavkin.todoist.api.RawRequest
import shapeless.test.illTyped
import shapeless.{::, HNil}

class IsResourceSpec extends FunSuite with Checkers {

  test("IsResource") {
    implicit val i1 = HasRawRequest[Int](Vector("Int"))
    implicit val i2 = HasRawRequest[String](Vector("String"))

    HasRawRequest[Int]
    HasRawRequest[String]
    HasRawRequest[Int :: String :: HNil]

    illTyped("""IsResource[Boolean]""")
    illTyped("""IsResource[Boolean :: Int :: HNil]""")
    illTyped("""IsResource[Int :: Boolean :: HNil]""")
  }

  test("IsResource combinates") {
    check { (a: RawRequest, b: RawRequest) =>
      implicit val i1 = HasRawRequest[Int](a)
      implicit val i2 = HasRawRequest[String](b)
      HasRawRequest[Int :: String :: HNil].rawRequest == a ++ b
    }
  }
}

