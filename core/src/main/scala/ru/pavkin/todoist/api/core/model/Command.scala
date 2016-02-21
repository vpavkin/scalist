package ru.pavkin.todoist.api.core.model

import java.util.UUID

import ru.pavkin.todoist.api.core.tags
import shapeless.tag.@@

sealed trait Command {
  val uuid: UUID
}

sealed trait SimpleCommand extends Command

sealed trait TempIdCommand extends Command {
  val tempId: UUID
}

case class AddProject(name: String,
                      color: Option[ProjectColor] = None,
                      indent: Option[Indent] = None,
                      order: Option[Int] = None)

case class UpdateProject(id: Int @@ tags.ProjectId,
                         name: Option[String] = None,
                         color: Option[ProjectColor] = None,
                         indent: Option[Indent] = None,
                         order: Option[Int] = None,
                         isCollapsed: Option[Boolean] = None)

case class AddTaskToInbox(content: String,
                          date: Option[TaskDate] = None,
                          priority: Option[Priority] = None,
                          indent: Option[Indent] = None,
                          order: Option[Int] = None,
                          dayOrder: Option[Int] = None,
                          isCollapsed: Option[Boolean] = None,
                          labels: List[Int @@ tags.LabelId] = Nil)

case class AddTask(content: String,
                   projectId: Int @@ tags.ProjectId,
                   date: Option[TaskDate] = None,
                   priority: Option[Priority] = None,
                   indent: Option[Indent] = None,
                   order: Option[Int] = None,
                   dayOrder: Option[Int] = None,
                   isCollapsed: Option[Boolean] = None,
                   labels: List[Int @@ tags.LabelId] = Nil,
                   assignedBy: Option[Int @@ tags.UserId] = None,
                   responsible: Option[Int @@ tags.UserId] = None)

case class UpdateTask(id: Int @@ tags.TaskId,
                      content: String,
                      projectId: Int @@ tags.ProjectId,
                      date: Option[TaskDate] = None,
                      priority: Option[Priority] = None,
                      indent: Option[Indent] = None,
                      order: Option[Int] = None,
                      dayOrder: Option[Int] = None,
                      isCollapsed: Option[Boolean] = None,
                      labels: List[Int @@ tags.LabelId] = Nil,
                      assignedBy: Option[Int @@ tags.UserId] = None,
                      responsible: Option[Int @@ tags.UserId] = None)
