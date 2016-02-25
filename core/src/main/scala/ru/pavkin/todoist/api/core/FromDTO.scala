package ru.pavkin.todoist.api.core

import cats.Functor
import cats.syntax.functor._
import ru.pavkin.todoist.api
import ru.pavkin.todoist.api.core.model._
import ru.pavkin.todoist.api.core.tags.{LabelId, UserId, ProjectId}
import ru.pavkin.todoist.api.utils.Produce
import shapeless.{CNil, Inr, Inl, tag}

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
        tag[ProjectId](a.id),
        tag[UserId](a.user_id),
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
      tag[LabelId](a.id),
      tag[UserId](a.uid),
      a.name,
      a.color.toLabelColor,
      a.item_order,
      a.is_deleted.toBool
    )
  )

  // command results

  implicit val singleCommandStatusFromDTO: FromDTO[dto.RawItemStatus, model.SingleCommandStatus] = FromDTO {
    case Inl(_) => CommandSuccess
    case Inr(Inl(e)) => CommandFailure(e.error_code, e.error)
    case Inr(Inr(cNil)) => api.unexpected
  }

  implicit val commandStatusFromDTO: FromDTO[dto.RawCommandStatus, model.CommandStatus] = FromDTO {
    case Inl(_) => CommandSuccess
    case Inr(Inl(e)) => CommandFailure(e.error_code, e.error)
    case Inr(Inr(Inl(s))) => MultiItemCommandStatus(s.map {
      case (id, status) =>
        id.toInt -> singleCommandStatusFromDTO.produce(status)
    })
    case Inr(Inr(Inr(cNil))) => api.unexpected
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
      case Inr(Inr(cNil)) => api.unexpected
    }
}
