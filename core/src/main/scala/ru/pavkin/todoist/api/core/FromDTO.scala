package ru.pavkin.todoist.api.core

import cats.Functor
import cats.syntax.functor._
import ru.pavkin.todoist.api
import ru.pavkin.todoist.api.core.model._
import ru.pavkin.todoist.api.core.tags.syntax._
import ru.pavkin.todoist.api.utils.Produce
import shapeless.{Inl, Inr}
import FromDTO.syntax._

trait FromDTO[DTO, Model] extends Produce[DTO, Model]

object FromDTO {

  def apply[DTO, Model](f: DTO => Model): FromDTO[DTO, Model] = new FromDTO[DTO, Model] {
    def produce(a: DTO): Model = f(a)
  }

  private implicit class BinaryIntOps(a: Int) {
    def toBool = a match {
      case 1 => true
      case 0 => false
      case _ => api.unexpected
    }
  }

  private implicit class ProjectColorIntOps(a: Int) {
    def toProjectColor = ProjectColor.unsafeBy(a)
  }

  private implicit class IndentIntOps(a: Int) {
    def toIndent = Indent.unsafeBy(a)
  }

  private implicit class LabelColorIntOps(a: Int) {
    def toLabelColor = LabelColor.unsafeBy(a)
  }

  private implicit class BoolOptionOps(a: Option[Boolean]) {
    def toBool = a.exists(identity)
  }

  object syntax {
    implicit class Ops[DTO, Model](a: DTO)(implicit F: FromDTO[DTO, Model]) {
      def toModel: Model = F.produce(a)
    }
  }

  implicit def functorFromDTO[F[_] : Functor, DTO, Model](implicit F: FromDTO[DTO, Model]): FromDTO[F[DTO], F[Model]] =
    FromDTO(_.map(F.produce))

  implicit val projectsFromDTO: FromDTO[dto.Project, Project] = FromDTO(a =>
    if (!a.is_archived.toBool) {
      model.RegularProject(
        a.id.projectId,
        a.user_id.userId,
        a.name,
        a.color.toProjectColor,
        a.indent.toIndent,
        a.item_order,
        a.collapsed.toBool,
        a.shared,
        a.is_deleted.toBool,
        a.inbox_project.toBool,
        a.team_inbox.toBool
      )
    } else {
      api.unexpected
    }
  )

  implicit val labelsFromDTO: FromDTO[dto.Label, model.Label] = FromDTO(a =>
    model.Label(
      a.id.labelId,
      a.uid.userId,
      a.name,
      a.color.toLabelColor,
      a.item_order,
      a.is_deleted.toBool
    )
  )

  implicit val tasksFromDTO: FromDTO[dto.Task, model.Task] = FromDTO(a =>
    model.Task(
      a.id.taskId,
      a.user_id.userId,
      a.project_id.projectId,
      a.content,
      a.due_date_utc
        .flatMap(TodoistDate.parse)
        .map(TaskDate(a.date_string, DateLanguage.unsafeBy(a.date_lang), _)),
      Priority.unsafeBy(a.priority),
      Indent.unsafeBy(a.indent),
      a.item_order,
      a.day_order,
      a.collapsed.toBool,
      a.labels.map(_.labelId),
      a.assigned_by_uid.map(_.userId),
      a.responsible_uid.map(_.userId),
      a.checked.toBool,
      a.in_history.toBool,
      a.is_deleted.toBool,
      a.is_archived.toBool,
      TodoistDate.parse(a.date_added).getOrElse(api.unexpected)
    )
  )

  implicit val filesFromDTO: FromDTO[dto.FileAttachment, model.FileAttachment] = FromDTO(a =>
    model.FileAttachment(
      a.file_name,
      a.file_size,
      a.file_type,
      a.file_url,
      UploadState.unsafe(a.upload_state)
    )
  )

  implicit val notesFromDTO: FromDTO[dto.Note, model.Note] = FromDTO(a =>
    model.Note(
      a.id.noteId,
      a.posted_uid.userId,
      a.item_id.taskId,
      a.project_id.projectId,
      a.content,
      a.file_attachment.map(_.toModel),
      a.uids_to_notify.toList.flatten.map(_.userId),
      a.is_deleted.toBool,
      a.is_archived.toBool,
      TodoistDate.parse(a.posted).getOrElse(api.unexpected)
    )
  )

  // command results

  implicit val singleCommandStatusFromDTO: FromDTO[dto.RawItemStatus, model.SingleCommandStatus] = FromDTO {
    case Inl(_) => CommandSuccess
    case Inr(Inl(e)) => CommandFailure(e.error_code, e.error)
    case Inr(Inr(cNil)) => cNil.impossible
  }

  implicit val commandStatusFromDTO: FromDTO[dto.RawCommandStatus, model.CommandStatus] = FromDTO {
    case Inl(_) => CommandSuccess
    case Inr(Inl(e)) => CommandFailure(e.error_code, e.error)
    case Inr(Inr(Inl(s))) => MultiItemCommandStatus(s.map {
      case (id, status) =>
        id.toInt -> singleCommandStatusFromDTO.produce(status)
    })
    case Inr(Inr(Inr(cNil))) => cNil.impossible
  }

  def tempIdCommandStatusFromDTO(command: TempIdCommand[_],
                                 result: dto.RawCommandResult): Option[model.TempIdCommandResult] =
    result.SyncStatus.get(command.uuid.toString).flatMap {
      case Inl(_) =>
        result.TempIdMapping
          .flatMap(_.get(command.tempId.toString))
          .map(TempIdSuccess(command.tempId, _))
          .map(TempIdCommandResult(command.uuid, _))
      case Inr(Inl(e)) =>
        Some(TempIdCommandResult(command.uuid, TempIdFailure(e.error_code, e.error)))
      case Inr(Inr(Inl(multipleCommandStatus))) => api.unexpected
      case Inr(Inr(Inr(cNil))) => cNil.impossible
    }
}
