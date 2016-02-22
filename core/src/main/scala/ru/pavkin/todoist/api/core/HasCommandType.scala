package ru.pavkin.todoist.api.core

import ru.pavkin.todoist.api.core.dto.IsResourceId
import ru.pavkin.todoist.api.core.model._

trait HasCommandType[T] {
  def commandType: String
}

object HasCommandType {

  def apply[T](s: String): HasCommandType[T] = new HasCommandType[T] {
    def commandType: String = s
  }

  implicit val addProject: HasCommandType[AddProject] = HasCommandType("project_add")
  implicit def addTask[T: IsResourceId]: HasCommandType[AddTask[T]] = HasCommandType("item_add")
  implicit val addTaskToInbox: HasCommandType[AddTaskToInbox] = HasCommandType("item_add")
  implicit val addLabel: HasCommandType[AddLabel] = HasCommandType("label_add")
}
