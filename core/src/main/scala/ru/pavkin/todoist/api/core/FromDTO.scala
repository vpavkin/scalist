package ru.pavkin.todoist.api.core

import java.util.{TimeZone, Date}

import cats.Functor
import cats.syntax.functor._
import ru.pavkin.todoist.api
import ru.pavkin.todoist.api.core.model._
import ru.pavkin.todoist.api.core.tags.syntax._
import ru.pavkin.todoist.api.utils.Produce
import shapeless.{Inl, Inr}
import FromDTO.syntax._

import scala.util.Try

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

  private implicit class ModelIntOps(a: Int) {
    def toProjectColor = ProjectColor.unsafeBy(a)
    def toIndent = Indent.unsafeBy(a)
    def toLabelColor = LabelColor.unsafeBy(a)
    def toDayOfWeek = DayOfWeek.unsafeBy(a)
  }

  private implicit class BoolOptionOps(a: Option[Boolean]) {
    def toBool = a.exists(identity)
  }

  private implicit class StringDateOps(a: String) {
    def toDate: Date = TodoistDate.parse(a).getOrElse(api.unexpected)
  }

  object syntax {
    implicit class Ops[DTO, Model](a: DTO)(implicit F: FromDTO[DTO, Model]) {
      def toModel: Model = F.produce(a)
    }
  }

  implicit def functorFromDTO[F[_] : Functor, DTO, Model](implicit F: FromDTO[DTO, Model]): FromDTO[F[DTO], F[Model]] =
    FromDTO(_.map(F.produce))

  private def taskDateFromDTO(due_date_utc: Option[String],
                              date_string: Option[String],
                              date_lang: Option[String]): Option[model.TaskDate] = for {
    date <- due_date_utc.flatMap(TodoistDate.parse)
    lang <- date_lang.map(DateLanguage.unsafeBy)
  } yield model.TaskDate(date_string, lang, date)


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
      taskDateFromDTO(a.due_date_utc, a.date_string, Some(a.date_lang)),
      Priority.unsafeBy(a.priority),
      a.indent.toIndent,
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
      a.date_added.toDate
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
      a.posted.toDate
    )
  )

  implicit val filtersFromDTO: FromDTO[dto.Filter, model.Filter] = FromDTO(a =>
    model.Filter(
      a.id.filterId,
      a.name,
      a.query,
      a.color.toLabelColor,
      a.item_order,
      a.is_deleted.toBool
    )
  )

  implicit val remindersFromDTO: FromDTO[dto.Reminder, model.Reminder] = FromDTO(a =>
    (a.`type` match {
      case "relative" | "absolute" => for {
        dueDate <- taskDateFromDTO(a.due_date_utc, a.date_string, a.date_lang)
        service <- a.service.map(ReminderService.unsafeBy)
      } yield if (a.`type` == "relative")
        RelativeTimeBasedReminder(
          a.id.reminderId,
          a.notify_uid.userId,
          a.item_id.taskId,
          service,
          dueDate,
          a.minute_offset.orElse(a.mm_offset).getOrElse(api.unexpected),
          a.is_deleted.toBool
        )
      else
        AbsoluteTimeBasedReminder(
          a.id.reminderId,
          a.notify_uid.userId,
          a.item_id.taskId,
          service,
          dueDate,
          a.is_deleted.toBool
        )
      case "location" => for {
        locName <- a.name
        lat <- a.loc_lat.flatMap(s => Try(s.toDouble).toOption)
        lon <- a.loc_long.flatMap(s => Try(s.toDouble).toOption)
        radius <- a.radius
        trigger <- a.loc_trigger.map(LocationBasedReminder.TriggerKind.unsafeBy)
      } yield LocationBasedReminder(
        a.id.reminderId,
        a.notify_uid.userId,
        a.item_id.taskId,
        locName,
        lat,
        lon,
        trigger,
        radius,
        a.is_deleted.toBool
      )
      case _ => api.unexpected
    }).getOrElse(api.unexpected)
  )

  def timezoneToDTO(id: String, offset: dto.TimeZoneOffset): TimeZone = {
    val idBased = TimeZone.getTimeZone(id)
    if (idBased.getID == "GMT" && (offset.hours + offset.minutes > 0))
      TimeZone.getTimeZone(s"GMT${offset.gmtString}")
    else
      idBased
  }

  implicit val usersToDTO: FromDTO[dto.User, model.User] = FromDTO(a =>
    model.User(
      a.id.userId,
      a.email,
      a.full_name,
      a.inbox_project.projectId,
      timezoneToDTO(a.timezone, a.tz_offset),
      a.start_page,
      a.start_day.toDayOfWeek,
      a.next_week.toDayOfWeek,
      TimeFormat.unsafeBy(a.time_format),
      DateFormat.unsafeBy(a.date_format),
      ProjectsSortOrder.unsafeBy(a.sort_order),
      a.has_push_reminders,
      a.default_reminder.map(ReminderService.unsafeBy),
      a.auto_reminder,
      a.mobile_number,
      a.mobile_host,
      a.completed_count,
      a.completed_today,
      a.karma,
      a.premium_until.flatMap(TodoistDate.parse),
      a.is_biz_admin,
      a.business_account_id,
      a.beta.toBool,
      a.is_dummy.toBool,
      a.join_date.toDate,
      Theme.unsafeBy(a.theme),
      UserAvatars(a.avatar_small, a.avatar_medium, a.avatar_big, a.avatar_s640)
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
