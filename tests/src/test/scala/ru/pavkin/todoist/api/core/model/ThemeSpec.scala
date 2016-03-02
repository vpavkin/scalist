package ru.pavkin.todoist.api.core.model

import org.scalacheck.Arbitrary._
import org.scalacheck.Gen
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalatest.{FunSuite, Matchers}

class ThemeSpec extends FunSuite with Matchers with GeneratorDrivenPropertyChecks {

  test("Theme.unsafeBy returns theme if it exists") {
    forAll(Gen.choose(0, 9)) { (n: Int) =>
      whenever(n >= 0 && n <= 9) {
        noException should be thrownBy Theme.unsafeBy(n)
        LabelColor.unsafeBy(n).isPremium shouldBe n >= 8
      }
    }
    forAll { (n: Int) =>
      whenever(n < 0 || n > 9) {
        an[Exception] should be thrownBy Theme.unsafeBy(n)
      }
    }
  }
}

