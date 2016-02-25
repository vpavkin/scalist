package ru.pavkin.todoist.api.core.model

import org.scalacheck.Arbitrary._
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalatest.{FunSuite, Matchers}

class IndentSpec extends FunSuite with Matchers with GeneratorDrivenPropertyChecks {

  test("Indent.by returns indent if it exists") {
    val gen = arbitrary[Int].map(_ % 5).map(math.abs)
    forAll(gen) { (n: Int) =>
      whenever(n >= 1 && n <= 4) {
        noException should be thrownBy Indent.unsafeBy(n)
      }
    }
    forAll { (n: Int) =>
      whenever(n < 1 || n > 4) {
        an[Exception] should be thrownBy Indent.unsafeBy(n)
      }
    }
  }
}

