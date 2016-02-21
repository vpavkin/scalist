package ru.pavkin.todoist.api.core

import cats.Functor
import cats.syntax.functor._
import ru.pavkin.todoist.api
import ru.pavkin.todoist.api.core.model._
import ru.pavkin.todoist.api.core.tags.{LabelId, UserId, ProjectId}
import ru.pavkin.todoist.api.utils.Produce
import shapeless.tag

trait FromDTO[DTO, Model] extends Produce[DTO, Model]

object FromDTO {

  private implicit class BinaryIntOps(a: Int) {
    def toBool = a match {
      case 1 => true
      case 0 => false
      case _ => api.unexpected
    }
  }

  private implicit class ProjectColorIntOps(a: Int) {
    def toProjectColor = ProjectColor(a)
  }

  private implicit class IndentIntOps(a: Int) {
    def toIndent = Indent.by(a)
  }

  private implicit class LabelColorIntOps(a: Int) {
    def toLabelColor = LabelColor(a)
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
    new FromDTO[F[DTO], F[Model]] {
      def produce(a: F[DTO]): F[Model] = a.map(F.produce)
    }

  implicit val projectsFromDTO: FromDTO[dto.Project, Project] = new FromDTO[dto.Project, model.Project] {
    def produce(a: dto.Project): model.Project =
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
  }

  implicit val labelsFromDTO = new FromDTO[dto.Label, model.Label] {
    def produce(a: dto.Label): model.Label =
      model.Label(
        tag[LabelId](a.id),
        tag[UserId](a.uid),
        a.name,
        a.color.toLabelColor,
        a.item_order,
        a.is_deleted.toBool
      )
  }
}
