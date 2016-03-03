package ru.pavkin.todoist.api.core.model

import java.util.UUID

import ru.pavkin.todoist.api.core.IsResourceId
import ru.pavkin.todoist.api.core.tags
import ru.pavkin.todoist.api.core.tags.syntax._
import shapeless.tag.@@

sealed trait Command {self =>
  val uuid: UUID
}

sealed trait SimpleCommand extends Command

sealed trait TempIdCommand[Tag] extends Command {
  val tempId: UUID @@ Tag
}

case class AddProject(name: String,
                      color: Option[ProjectColor] = None,
                      indent: Option[Indent] = None,
                      order: Option[Int] = None,
                      uuid: UUID = UUID.randomUUID(),
                      tempId: UUID @@ tags.ProjectId = UUID.randomUUID().projectId)
  extends TempIdCommand[tags.ProjectId]

case class AddTaskToInbox(content: String,
                          date: Option[TaskDate] = None,
                          priority: Option[Priority] = None,
                          indent: Option[Indent] = None,
                          order: Option[Int] = None,
                          dayOrder: Option[Int] = None,
                          isCollapsed: Option[Boolean] = None,
                          labels: List[Int @@ tags.LabelId] = Nil,
                          uuid: UUID = UUID.randomUUID(),
                          tempId: UUID @@ tags.TaskId = UUID.randomUUID().taskId)
  extends TempIdCommand[tags.TaskId]

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
                                    tempId: UUID @@ tags.TaskId = UUID.randomUUID().taskId)
  extends TempIdCommand[tags.TaskId]

case class AddLabel(name: String,
                    color: Option[LabelColor] = None,
                    order: Option[Int] = None,
                    uuid: UUID = UUID.randomUUID(),
                    tempId: UUID @@ tags.LabelId = UUID.randomUUID().labelId)
  extends TempIdCommand[tags.LabelId]

case class UpdateProject[A: IsResourceId](id: A @@ tags.ProjectId,
                                          name: Option[String] = None,
                                          color: Option[ProjectColor] = None,
                                          indent: Option[Indent] = None,
                                          order: Option[Int] = None,
                                          isCollapsed: Option[Boolean] = None,
                                          uuid: UUID = UUID.randomUUID())
  extends SimpleCommand

case class UpdateTask[A: IsResourceId](id: A @@ tags.TaskId,
                                       content: Option[String] = None,
                                       date: Option[TaskDate] = None,
                                       priority: Option[Priority] = None,
                                       indent: Option[Indent] = None,
                                       order: Option[Int] = None,
                                       dayOrder: Option[Int] = None,
                                       isCollapsed: Option[Boolean] = None,
                                       labels: List[Int @@ tags.LabelId] = Nil,
                                       assignedBy: Option[Int @@ tags.UserId] = None,
                                       responsible: Option[Int @@ tags.UserId] = None,
                                       uuid: UUID = UUID.randomUUID())
  extends SimpleCommand

case class UpdateLabel[A: IsResourceId](id: A @@ tags.LabelId,
                                        name: Option[String] = None,
                                        color: Option[LabelColor] = None,
                                        order: Option[Int] = None,
                                        uuid: UUID = UUID.randomUUID())
  extends SimpleCommand

case class DeleteTasks[A: IsResourceId](tasks: List[A @@ tags.TaskId],
                                        uuid: UUID = UUID.randomUUID())
  extends SimpleCommand

case class DeleteProjects[A: IsResourceId](projects: List[A @@ tags.ProjectId],
                                           uuid: UUID = UUID.randomUUID())
  extends SimpleCommand

case class DeleteLabel[A: IsResourceId](label: A @@ tags.LabelId,
                                        uuid: UUID = UUID.randomUUID())
  extends SimpleCommand

case class MoveTasks(tasks: Map[Int @@ tags.ProjectId, List[Int @@ tags.TaskId]],
                     toProject: Int @@ tags.ProjectId,
                     uuid: UUID = UUID.randomUUID())
  extends SimpleCommand

case class CloseTask[A: IsResourceId](task: A @@ tags.TaskId,
                                      uuid: UUID = UUID.randomUUID())
  extends SimpleCommand

case class UncompleteTasks[A: IsResourceId](tasks: List[A @@ tags.TaskId],
                                            uuid: UUID = UUID.randomUUID())
  extends SimpleCommand

case class ArchiveProjects[A: IsResourceId](projects: List[A @@ tags.ProjectId],
                                            uuid: UUID = UUID.randomUUID())
  extends SimpleCommand

case class UnarchiveProjects[A: IsResourceId](projects: List[A @@ tags.ProjectId],
                                              uuid: UUID = UUID.randomUUID())
  extends SimpleCommand
