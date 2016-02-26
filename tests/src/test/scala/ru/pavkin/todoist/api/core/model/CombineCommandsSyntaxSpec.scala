package ru.pavkin.todoist.api.core.model

import java.util.UUID

import org.scalacheck.Arbitrary
import org.scalatest.prop.Checkers
import org.scalatest.{FunSuite, Matchers}
import ru.pavkin.todoist.api.core.model.util.CombineCommands
import ru.pavkin.todoist.api.core.tags.syntax._
import shapeless.HNil
import shapeless.test.illTyped

class CombineCommandsSyntaxSpec extends FunSuite with Matchers with Checkers with CombineCommands.Syntax {

  implicit val gen = Arbitrary(org.scalacheck.Gen.uuid)

  val c1 = AddProject("Project")
  val c2 = AddLabel("Label")
  val c3 = AddTask("Task", 1.projectId)

  test("Commands combine in HList") {
    c1 :+ c2 shouldBe c1 :: c2 :: HNil
  }

  test("HList :+ works with combined commands") {
    c1 :+ c2 :+ c3 shouldBe c1 :: c2 :: c3 :: HNil
  }

  test("forIt creates temp_id dependant command") {
    val uuid = UUID.randomUUID
    val tempId = UUID.randomUUID
    c1.forIt(AddTask("task", _, uuid = uuid, tempId = tempId.taskId)) shouldBe
      AddTask[UUID]("task", c1.tempId, uuid = uuid, tempId = tempId.taskId)
  }

  test("andForIt creates temp_id dependant command and adds it to the parent") {
    check { (uuid: UUID, tempId: UUID) =>
      c1.andForIt(AddTask("task", _, uuid = uuid, tempId = tempId.taskId)) ==
        (c1 :: AddTask[UUID]("task", c1.tempId, uuid = uuid, tempId = tempId.taskId) :: HNil)
    }
  }

  test("andForItAll creates temp_id dependant commands and adds them to the parent") {
    check { (uuid1: UUID, tempId1: UUID, uuid2: UUID, tempId2: UUID) =>
      c1.andForItAll(id =>
        AddTask("task", id, uuid = uuid1, tempId = tempId1.taskId) :+
          AddTask("task2", id, uuid = uuid2, tempId = tempId2.taskId)
      ) ==
        (c1 ::
          AddTask[UUID]("task", c1.tempId, uuid = uuid1, tempId = tempId1.taskId) ::
          AddTask[UUID]("task2", c1.tempId, uuid = uuid2, tempId = tempId2.taskId) ::
          HNil)
    }
  }

  test("Only Commands combine") {
    illTyped("""12 :+ 13""")
    illTyped(""" true :+ 2 """)
  }
}

