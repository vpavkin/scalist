package ru.pavkin.todoist.api.core

import ru.pavkin.todoist.api.core.model._

trait HasCommandType[T] {
  def commandType: String
}

object HasCommandType {

  object syntax {
    implicit class CommandTypeOps[T](o: T)(implicit ev: HasCommandType[T]) {
      def commandType = ev.commandType
    }
  }

  def apply[T](s: String): HasCommandType[T] = new HasCommandType[T] {
    def commandType: String = s
  }

  implicit val addProject: HasCommandType[AddProject] = HasCommandType("project_add")
  implicit def addTask[T: IsResourceId]: HasCommandType[AddTask[T]] = HasCommandType("item_add")
  implicit val addTaskToInbox: HasCommandType[AddTaskToInbox] = HasCommandType("item_add")
  implicit val addLabel: HasCommandType[AddLabel] = HasCommandType("label_add")

  implicit def updateProject[T: IsResourceId]: HasCommandType[UpdateProject[T]] = HasCommandType("project_update")
  implicit def updateTask[T: IsResourceId]: HasCommandType[UpdateTask[T]] = HasCommandType("item_update")
  implicit def updateLabel[T: IsResourceId]: HasCommandType[UpdateLabel[T]] = HasCommandType("label_update")

}
