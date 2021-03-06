package ru.pavkin.todoist.api.core

import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalatest.{FunSuite, Matchers}
import ru.pavkin.todoist.api.core.HasCommandType.syntax._
import ru.pavkin.todoist.api.core.model.LocationBasedReminder.TriggerKind
import ru.pavkin.todoist.api.core.model._
import ru.pavkin.todoist.api.core.tags.syntax._
import shapeless.test.illTyped

class HasCommandTypeSpec extends FunSuite with Matchers with GeneratorDrivenPropertyChecks {

  test("Valid commands have types") {
    AddProject("1").commandType shouldBe "project_add"
    AddLabel("1").commandType shouldBe "label_add"
    AddTask[Int]("1", 1.projectId).commandType shouldBe "item_add"
    AddTaskToInbox("1").commandType shouldBe "item_add"
    AddFilter("1", "1").commandType shouldBe "filter_add"
    AddNote[Int]("1", 1.taskId).commandType shouldBe "note_add"
    AddRelativeTimeBasedReminder[Int](
      1.taskId,
      ReminderService.Push,
      ReminderPeriod.min30
    ).commandType shouldBe "reminder_add"
    AddLocationBasedReminder[Int](
      1.taskId,
      "1",
      1.0,
      1.0,
      TriggerKind.Enter,
      100
    ).commandType shouldBe "reminder_add"
    UpdateProject[Int](1.projectId).commandType shouldBe "project_update"
    UpdateLabel[Int](1.labelId).commandType shouldBe "label_update"
    UpdateTask[Int](1.taskId).commandType shouldBe "item_update"
    UpdateFilter[Int](1.filterId).commandType shouldBe "filter_update"
    UpdateNote[Int](1.noteId).commandType shouldBe "note_update"

    DeleteProjects[Int](List(1.projectId)).commandType shouldBe "project_delete"
    DeleteTasks[Int](List(1.taskId)).commandType shouldBe "item_delete"
    DeleteLabel[Int](1.labelId).commandType shouldBe "label_delete"
    DeleteNote[Int](1.noteId).commandType shouldBe "note_delete"
    DeleteFilter[Int](1.filterId).commandType shouldBe "filter_delete"
    DeleteReminder[Int](1.reminderId).commandType shouldBe "reminder_delete"

    CloseTask[Int](1.taskId).commandType shouldBe "item_close"
    MoveTasks(Map.empty, 1.projectId).commandType shouldBe "item_move"
    UncompleteTasks[Int](List(1.taskId)).commandType shouldBe "item_uncomplete"
    ArchiveProjects[Int](List(1.projectId)).commandType shouldBe "project_archive"
    UnarchiveProjects[Int](List(1.projectId)).commandType shouldBe "project_unarchive"
  }

  test("Other types don't have commandType") {
    illTyped("""1.commandType""")
  }
}

