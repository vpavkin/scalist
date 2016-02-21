package ru.pavkin.todoist.api.core.model

import java.util.UUID

import ru.pavkin.todoist.api.core.dto.IsResourceId
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
                      order: Option[Int] = None,
                      uuid: UUID = UUID.randomUUID(),
                      tempId: UUID = UUID.randomUUID()) extends TempIdCommand

case class AddTaskToInbox(content: String,
                          date: Option[TaskDate] = None,
                          priority: Option[Priority] = None,
                          indent: Option[Indent] = None,
                          order: Option[Int] = None,
                          dayOrder: Option[Int] = None,
                          isCollapsed: Option[Boolean] = None,
                          labels: List[Int @@ tags.LabelId] = Nil,
                          uuid: UUID = UUID.randomUUID(),
                          tempId: UUID = UUID.randomUUID()) extends TempIdCommand

case class AddTask[A: IsResourceId](content: String,
                                    projectId: A @@ tags.ProjectId,
                                    date: Option[TaskDate] = None,
                                    priority: Option[Priority] = None,
                                    indent: Option[Indent] = None,
                                    order: Option[Int] = None,
                                    dayOrder: Option[Int] = None,
                                    isCollapsed: Option[Boolean] = None,
                                    labels: List[Int @@ tags.LabelId] = Nil,
                                    assignedBy: Option[Int @@ tags.UserId] = None,
                                    responsible: Option[Int @@ tags.UserId] = None,
                                    uuid: UUID = UUID.randomUUID(),
                                    tempId: UUID = UUID.randomUUID()) extends TempIdCommand

case class AddLabel(name: String,
                    color: Option[LabelColor] = None,
                    order: Option[Int] = None,
                    uuid: UUID = UUID.randomUUID(),
                    tempId: UUID = UUID.randomUUID()) extends TempIdCommand

// todo: after this line

case class UpdateProject(id: Int @@ tags.ProjectId,
                         name: Option[String] = None,
                         color: Option[ProjectColor] = None,
                         indent: Option[Indent] = None,
                         order: Option[Int] = None,
                         isCollapsed: Option[Boolean] = None)

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
