package ru.pavkin.todoist.api.core.model

import org.scalacheck.Arbitrary._
import org.scalacheck.Gen
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalatest.{FunSuite, Matchers}

class TimeFormatSpec extends FunSuite with Matchers with GeneratorDrivenPropertyChecks {

  test("TimeFormat.unsafeBy returns format if it exists") {
    forAll(Gen.choose(0, 1)) { (n: Int) =>
      whenever(n >= 0 && n <= 1) {
        noException should be thrownBy TimeFormat.unsafeBy(n)
      }
    }
    forAll { (n: Int) =>
      whenever(n < 0 || n > 1) {
        an[Exception] should be thrownBy TimeFormat.unsafeBy(n)
      }
    }
  }
}

