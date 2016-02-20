package ru.pavkin.todoist.api.core.model

import java.util.UUID

trait Command {
  val uuid: UUID
}

trait TempIdCommand {

}

case class AddProject(name: String,
                      color: Option[ProjectColor] = None,
                      indent: Option[Indent] = None,
                      order: Option[Int] = None)

case class UpdateProject(id: ProjectId,
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
                          labels: List[LabelId] = Nil)

case class AddTask(content: String,
                   projectId: ProjectId,
                   date: Option[TaskDate] = None,
                   priority: Option[Priority] = None,
                   indent: Option[Indent] = None,
                   order: Option[Int] = None,
                   dayOrder: Option[Int] = None,
                   isCollapsed: Option[Boolean] = None,
                   labels: List[LabelId] = Nil,
                   assignedBy: Option[UserId] = None,
                   responsible: Option[UserId] = None)

case class UpdateTask(id: TaskId,
                      content: String,
                      projectId: ProjectId,
                      date: Option[TaskDate] = None,
                      priority: Option[Priority] = None,
                      indent: Option[Indent] = None,
                      order: Option[Int] = None,
                      dayOrder: Option[Int] = None,
                      isCollapsed: Option[Boolean] = None,
                      labels: List[LabelId] = Nil,
                      assignedBy: Option[UserId] = None,
                      responsible: Option[UserId] = None)
