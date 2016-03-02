package ru.pavkin.todoist.api.core.model

import org.scalacheck.Arbitrary._
import org.scalacheck.Gen
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalatest.{FunSuite, Matchers}

class DayOfWeekSpec extends FunSuite with Matchers with GeneratorDrivenPropertyChecks {

  test("DayOfWeek.unsafeBy returns day if it exists") {
    forAll(Gen.choose(1, 7)) { (n: Int) =>
      whenever(n >= 1 && n <= 7) {
        noException should be thrownBy DayOfWeek.unsafeBy(n)
      }
    }
    forAll { (n: Int) =>
      whenever(n < 1 || n > 7) {
        an[Exception] should be thrownBy DayOfWeek.unsafeBy(n)
      }
    }
  }
}

