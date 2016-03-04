package ru.pavkin.todoist.api.core.model

import ru.pavkin.todoist.api
import ru.pavkin.todoist.api.core.tags
import shapeless.tag.@@

sealed trait ReminderService {

  import ReminderService._

  def name: String = this match {
    case Push => "push"
    case SMS => "mobile"
    case Email => "email"
  }
}

object ReminderService {

  case object Push extends ReminderService
  case object SMS extends ReminderService
  case object Email extends ReminderService

  def unsafeBy(code: String): ReminderService = code match {
    case "email" => Email
    case "mobile" => SMS
    case "push" => Push
    case _ => api.unexpected
  }
}

sealed trait Reminder {
  def id: Int @@ tags.ReminderId
  def subscriber: Int @@ tags.UserId
  def task: Int @@ tags.TaskId
  def isDeleted: Boolean
}

sealed trait TimeBasedReminder extends Reminder {
  def service: ReminderService
  def dueDate: TaskDate
}

case class RelativeTimeBasedReminder(id: Int @@ tags.ReminderId,
                                     subscriber: Int @@ tags.UserId,
                                     task: Int @@ tags.TaskId,
                                     service: ReminderService,
                                     dueDate: TaskDate,
                                     minutesBefore: Int,
                                     isDeleted: Boolean) extends TimeBasedReminder

case class AbsoluteTimeBasedReminder(id: Int @@ tags.ReminderId,
                                     subscriber: Int @@ tags.UserId,
                                     task: Int @@ tags.TaskId,
                                     service: ReminderService,
                                     dueDate: TaskDate,
                                     isDeleted: Boolean) extends TimeBasedReminder

case class LocationBasedReminder(id: Int @@ tags.ReminderId,
                                 subscriber: Int @@ tags.UserId,
                                 task: Int @@ tags.TaskId,
                                 locationName: String,
                                 latitude: Double,
                                 longitude: Double,
                                 triggerKind: LocationBasedReminder.TriggerKind,
                                 radiusInMeters: Int,
                                 isDeleted: Boolean) extends Reminder

object LocationBasedReminder {
  sealed trait TriggerKind {

    import TriggerKind._

    def name: String = this match {
      case Enter => "on_enter"
      case Leave => "on_leave"
    }
  }

  object TriggerKind {
    case object Enter extends TriggerKind
    case object Leave extends TriggerKind

    def unsafeBy(code: String): TriggerKind = code match {
      case "on_enter" => Enter
      case "on_leave" => Leave
    }
  }
}
