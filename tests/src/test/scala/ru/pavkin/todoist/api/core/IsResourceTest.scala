package ru.pavkin.todoist.api.core

import org.scalacheck.Properties
import org.scalacheck.Prop.forAll
import org.scalatest.{FlatSpec, Matchers}
import shapeless.test.illTyped
import shapeless.{::, HNil}

class IsResourceTest extends FlatSpec with Matchers {

  "IsResource" should "work" in {
    implicit val i1 = IsResource[Int](Vector("Int"))
    implicit val i2 = IsResource[String](Vector("String"))

    IsResource[Int]
    IsResource[String]
    IsResource[Int :: String :: HNil]

    illTyped("""IsResource[Boolean]""")
    illTyped("""IsResource[Boolean :: Int :: HNil]""")
    illTyped("""IsResource[Int :: Boolean :: HNil]""")
  }

}

object IsResourceSpec extends Properties("IsResource") {
  property("combinates") = forAll { (a: Vector[String], b: Vector[String]) =>
    implicit val i1 = IsResource[Int](a)
    implicit val i2 = IsResource[String](b)
    IsResource[Int :: String :: HNil].strings == a ++ b
  }
}
