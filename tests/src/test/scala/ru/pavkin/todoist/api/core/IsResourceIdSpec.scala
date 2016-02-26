package ru.pavkin.todoist.api.core

import java.util.UUID

import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalatest.{FunSuite, Matchers}
import shapeless.test.illTyped

class IsResourceIdSpec extends FunSuite with Matchers with GeneratorDrivenPropertyChecks {

  test("Int and UUID is resource id") {
    implicitly[IsResourceId[Int]]
    implicitly[IsResourceId[UUID]]
  }

  test("Others are not resource ids") {
    illTyped("""implicitly[IsResourceId[String]]""")
    illTyped("""implicitly[IsResourceId[Long]]""")
  }
}

