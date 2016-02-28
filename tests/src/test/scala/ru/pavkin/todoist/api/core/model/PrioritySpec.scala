package ru.pavkin.todoist.api.core.model

import org.scalacheck.Arbitrary._
import org.scalacheck.Gen
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalatest.{FunSuite, Matchers}

class PrioritySpec extends FunSuite with Matchers with GeneratorDrivenPropertyChecks {

  test("Priority.unsafeBy returns priority if it exists") {
    forAll(Gen.choose(1, 4)) { (n: Int) =>
      whenever(n >= 1 && n <= 4) {
        noException should be thrownBy Priority.unsafeBy(n)
      }
    }
    forAll { (n: Int) =>
      whenever(n < 1 || n > 4) {
        an[Exception] should be thrownBy Priority.unsafeBy(n)
      }
    }
  }
}

