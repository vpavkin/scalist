package ru.pavkin.todoist.api.core.model

import org.scalacheck.Arbitrary._
import org.scalacheck.Gen
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalatest.{FunSuite, Matchers}

class ProjectsSortOrderSpec extends FunSuite with Matchers with GeneratorDrivenPropertyChecks {

  test("ProjectSortOrder.unsafeBy returns order if it exists") {
    forAll(Gen.choose(0, 1)) { (n: Int) =>
      whenever(n >= 0 && n <= 1) {
        noException should be thrownBy ProjectsSortOrder.unsafeBy(n)
      }
    }
    forAll { (n: Int) =>
      whenever(n < 0 || n > 1) {
        an[Exception] should be thrownBy ProjectsSortOrder.unsafeBy(n)
      }
    }
  }
}

