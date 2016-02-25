package ru.pavkin.todoist.api.core.model

import org.scalacheck.Arbitrary._
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalatest.{FunSuite, Matchers}

class ProjectColorSpec extends FunSuite with Matchers with GeneratorDrivenPropertyChecks {

  test("ProjectColor.unsafeBy returns color if it exists") {
    val gen = arbitrary[Int].map(_ % 22).map(math.abs)
    forAll(gen) { (n: Int) =>
      whenever(n >= 0 && n <= 21) {
        noException should be thrownBy ProjectColor.unsafeBy(n)
        ProjectColor.unsafeBy(n).isPremium shouldBe n >= 12
      }
    }
    forAll { (n: Int) =>
      whenever(n < 0 || n > 21) {
        an[Exception] should be thrownBy ProjectColor.unsafeBy(n)
      }
    }
  }
}

