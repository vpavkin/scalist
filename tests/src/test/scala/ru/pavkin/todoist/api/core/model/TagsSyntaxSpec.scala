package ru.pavkin.todoist.api.core.model

import java.util.UUID

import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalatest.{FunSuite, Matchers}
import ru.pavkin.todoist.api.core.tags
import ru.pavkin.todoist.api.core.tags._
import shapeless.tag
import shapeless.test.illTyped

class TagsSyntaxSpec extends FunSuite
  with Matchers
  with GeneratorDrivenPropertyChecks
  with tags.Syntax {

  test("tags conversions work for resource ids") {
    forAll(arbitrary[Int]) { (i: Int) =>
      i.projectId shouldBe tag[ProjectId](i)
      i.labelId shouldBe tag[LabelId](i)
      i.userId shouldBe tag[UserId](i)
      i.taskId shouldBe tag[TaskId](i)
    }
    forAll(Gen.uuid) { (i: UUID) =>
      i.projectId shouldBe tag[ProjectId](i)
      i.labelId shouldBe tag[LabelId](i)
      i.userId shouldBe tag[UserId](i)
      i.taskId shouldBe tag[TaskId](i)
    }
  }

  test("tags conversions don't work for other types") {
    illTyped(""" "a".projectId """)
    illTyped(""" "a".labelId """)
    illTyped(""" true.taskId """)
    illTyped(""" 100L.userId""")
  }
}

