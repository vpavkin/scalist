package ru.pavkin.todoist.api.core.model

sealed abstract class TokenScope(val name: String)

object TokenScope {
  case object AddTasks extends TokenScope("task:add")
  case object Read extends TokenScope("data:read")
  case object ReadWrite extends TokenScope("data:read_write")
  case object Delete extends TokenScope("data:delete")
  case object DeleteProjects extends TokenScope("project:delete")
}




