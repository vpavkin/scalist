package ru.pavkin.todoist.api.core

import java.text.SimpleDateFormat

import cats.Functor
import cats.syntax.functor._
import ru.pavkin.todoist.api.core.dto.IsResourceId
import ru.pavkin.todoist.api.core.model.{AddLabel, AddTaskToInbox, AddTask, AddProject}
import ru.pavkin.todoist.api.utils.Produce

trait ToDTO[Model, DTO] extends Produce[Model, DTO]

object ToDTO {

  def apply[Model, DTO](f: Model => DTO): ToDTO[Model, DTO] = new ToDTO[Model, DTO] {
    def produce(a: Model): DTO = f(a)
  }

  object syntax {
    implicit class Ops[Model, DTO](a: Model)(implicit F: ToDTO[Model, DTO]) {
      def toDTO: DTO = F.produce(a)
    }
  }

  private implicit class ToIntBoolOps(b: Boolean) {
    def toInt = if (b) 1 else 0
  }

  private val dateFormatter = new SimpleDateFormat("EEE dd MMM yyyy HH:mm:ss Z")

  implicit def functorToDTO[F[_] : Functor, Model, DTO](implicit F: ToDTO[Model, DTO]): ToDTO[F[Model], F[DTO]] =
    new ToDTO[F[Model], F[DTO]] {
      def produce(a: F[Model]): F[DTO] = a.map(F.produce)
    }

  implicit val addProjectToDTO: ToDTO[AddProject, dto.AddProject] = ToDTO(a => dto.AddProject(
    a.name, a.color.map(_.code), a.indent.map(_.value), a.order
  ))

  implicit def addTaskToDTO[T: IsResourceId]: ToDTO[AddTask[T], dto.AddTask[T]] =
    ToDTO(a => dto.AddTask[T](
      a.content,
      a.projectId,
      a.date.map(_.text),
      a.date.map(_.language.code),
      a.date.map(_.dueDateUTC).map(dateFormatter.format),
      a.priority.map(_.value),
      a.indent.map(_.value),
      a.order,
      a.dayOrder,
      a.isCollapsed.map(_.toInt),
      a.labels,
      a.assignedBy,
      a.responsible
    ))

  implicit val addTaskToInboxToDTO: ToDTO[AddTaskToInbox, dto.AddTaskToInbox] =
    ToDTO(a => dto.AddTaskToInbox(
      a.content,
      a.date.map(_.text),
      a.date.map(_.language.code),
      a.date.map(_.dueDateUTC).map(dateFormatter.format),
      a.priority.map(_.value),
      a.indent.map(_.value),
      a.order,
      a.dayOrder,
      a.isCollapsed.map(_.toInt),
      a.labels
    ))

  implicit val addLabelToDTO: ToDTO[AddLabel, dto.AddLabel] =
    ToDTO(a => dto.AddLabel(
      a.name,
      a.color.map(_.code),
      a.order
    ))
}
