package ru.pavkin.todoist.api.utils

import org.scalatest.{FlatSpec, Matchers}
import shapeless.{::, HNil}
import shapeless.test.illTyped

class NotContainsTest extends FlatSpec with Matchers {

  "NotContains" should "work" in {
    NotContains[HNil, Int]
    NotContains[String :: Int :: HNil, Boolean]

    illTyped("""NotContains[Int::HNil, Int]""")
    illTyped("""NotContains[Boolean :: Int :: HNil, Int]""")
    illTyped("""NotContains[String :: Boolean :: Int :: HNil, String]""")
  }

}
