package ru.pavkin.todoist.api.core

import org.scalatest.FunSuite
import org.scalatest.prop.Checkers
import shapeless.test.{illTyped, typed}
import shapeless.{::, HNil}

class CommandReturnsSpec extends FunSuite with Checkers {

  implicit val i1 = new CommandReturns[Int] {
    type Result = String
  }
  implicit val i2 = new CommandReturns[String] {
    type Result = Boolean
  }

  test("CommandReturns") {

    implicitly[CommandReturns[Int]]
    implicitly[CommandReturns[String]]
    implicitly[CommandReturns.Aux[String :: Int :: HNil, Boolean :: String :: HNil]]
    implicitly[CommandReturns.Aux[Int :: String :: HNil, String :: Boolean :: HNil]]

    illTyped("""implicitly[CommandReturns[Boolean]]""")
    illTyped("""implicitly[CommandReturns[Boolean :: Int :: HNil]]""")
    illTyped("""implicitly[CommandReturns[Int :: Boolean :: HNil]]""")
  }
}

