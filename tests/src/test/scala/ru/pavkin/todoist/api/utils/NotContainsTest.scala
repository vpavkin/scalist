package ru.pavkin.todoist.api.utils

import org.scalatest.prop.Checkers
import org.scalatest.{FunSuite, FlatSpec, Matchers}
import shapeless.{::, HNil}
import shapeless.test.illTyped

class NotContainsTest extends FunSuite with Checkers {

  test("NotContains") {
    NotContains[HNil, Int]
    NotContains[String :: Int :: HNil, Boolean]

    illTyped("""NotContains[Int::HNil, Int]""")
    illTyped("""NotContains[Boolean :: Int :: HNil, Int]""")
    illTyped("""NotContains[String :: Boolean :: Int :: HNil, String]""")
  }

}
