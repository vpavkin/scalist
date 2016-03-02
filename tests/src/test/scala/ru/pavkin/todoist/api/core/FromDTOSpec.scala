package ru.pavkin.todoist.api.core

import java.util.{TimeZone, Date}

import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalatest.{FunSuite, Matchers}
import ru.pavkin.todoist.api
import ru.pavkin.todoist.api.core.FromDTO.syntax._
import ru.pavkin.todoist.api.core.dto.FileAttachment
import ru.pavkin.todoist.api.core.dto.Filter
import ru.pavkin.todoist.api.core.dto.Label
import ru.pavkin.todoist.api.core.dto.Label
import ru.pavkin.todoist.api.core.dto.Note
import ru.pavkin.todoist.api.core.dto.Project
import ru.pavkin.todoist.api.core.dto.Project
import ru.pavkin.todoist.api.core.dto.Reminder
import ru.pavkin.todoist.api.core.dto.Task
import ru.pavkin.todoist.api.core.dto.Task
import ru.pavkin.todoist.api.core.dto.User
import ru.pavkin.todoist.api.core.dto._
import ru.pavkin.todoist.api.core.model.TempIdCommandResult
import ru.pavkin.todoist.api.core.model.TempIdFailure
import ru.pavkin.todoist.api.core.model.TempIdSuccess
import ru.pavkin.todoist.api.core.tags.syntax._
import shapeless._

class FromDTOSpec extends FunSuite with Matchers with GeneratorDrivenPropertyChecks {

  val projectGen: Gen[Project] = for {
    id <- arbitrary[Int]
    uid <- arbitrary[Int]
    name <- arbitrary[String]
    color <- Gen.choose(0, 21)
    indent <- Gen.choose(1, 4)
    item_order <- arbitrary[Int]
    collapsed <- Gen.choose(0, 1)
    shared <- arbitrary[Boolean]
    is_deleted <- Gen.choose(0, 1)
    (is_archived, archived_date, archived_timestamp) <- Gen.choose(0, 1)
      .map(i => (
        i,
        if (i == 1) Some("Stub that will fail when date parsing is implemented") else None,
        if (i == 1) arbitrary[Int].sample.get else -1
        ))
    inbox_project <- arbitrary[Option[Boolean]]
    team_inbox <- arbitrary[Option[Boolean]]
  } yield Project(id, uid, name, color, indent, item_order, collapsed, shared, is_deleted,
    is_archived, archived_date, archived_timestamp, inbox_project, team_inbox)

  test("Project") {
    forAll(projectGen) { (p: Project) =>
      // will fail when archived projects support will be added
      if (p.is_archived == 0) {
        p.toModel shouldBe model.RegularProject(
          p.id.projectId, p.user_id.userId, p.name, model.ProjectColor.unsafeBy(p.color),
          model.Indent.unsafeBy(p.indent), p.item_order, p.collapsed == 1, p.shared, p.is_deleted == 1,
          p.inbox_project == Some(true), p.team_inbox == Some(true)
        )
      } else {
        an[Exception] should be thrownBy (p.toModel)
      }
    }
  }


  val labelGen: Gen[Label] = for {
    id <- arbitrary[Int]
    uid <- arbitrary[Int]
    name <- arbitrary[String]
    color <- Gen.choose(0, 12)
    item_order <- arbitrary[Int]
    is_deleted <- Gen.choose(0, 1)
  } yield Label(id, uid, name, color, item_order, is_deleted)

  test("Label") {
    forAll(labelGen) { (l: Label) =>
      l.toModel shouldBe model.Label(
        l.id.labelId, l.uid.userId, l.name, model.LabelColor.unsafeBy(l.color),
        l.item_order, l.is_deleted == 1
      )
    }
  }

  val taskGen: Gen[Task] = for {
    id <- arbitrary[Int]
    uid <- arbitrary[Int]
    projectId <- arbitrary[Int]
    content <- arbitrary[String]
    date_str <- arbitrary[Option[String]]
    date_lang <- Gen.oneOf("en", "da", "pl", "zh", "ko", "de", "pt", "ja", "it", "fr", "sv", "ru", "es", "nl")
    due_date <- arbitrary[Option[Date]].map(_.map(model.TodoistDate.format))
    priority <- Gen.choose(1, 4)
    indent <- Gen.choose(1, 4)
    item_order <- arbitrary[Int]
    day_order <- arbitrary[Int]
    collapsed <- Gen.choose(0, 1)
    labels <- Gen.listOfN(5, arbitrary[Int])
    assigned_by <- arbitrary[Option[Int]]
    responsible <- arbitrary[Option[Int]]
    checked <- Gen.choose(0, 1)
    in_history <- Gen.choose(0, 1)
    is_deleted <- Gen.choose(0, 1)
    is_archived <- Gen.choose(0, 1)
    date_added <- arbitrary[Date].map(model.TodoistDate.format)
  } yield Task(id, uid, projectId, content, date_str, date_lang, due_date, priority, indent, item_order,
    day_order, collapsed, labels, assigned_by, responsible,
    checked, in_history, is_deleted, is_archived, date_added)

  test("Task") {
    forAll(taskGen) { (t: Task) =>
      t.toModel shouldBe model.Task(
        t.id.taskId, t.user_id.userId, t.project_id.projectId, t.content,
        t.due_date_utc
          .flatMap(model.TodoistDate.parse)
          .map(dd => model.TaskDate(t.date_string, model.DateLanguage.unsafeBy(t.date_lang), dd)),
        model.Priority.unsafeBy(t.priority),
        model.Indent.unsafeBy(t.indent),
        t.item_order,
        t.day_order,
        t.collapsed == 1,
        t.labels.map(_.labelId),
        t.assigned_by_uid.map(_.userId),
        t.responsible_uid.map(_.userId),
        t.checked == 1,
        t.in_history == 1,
        t.is_deleted == 1,
        t.is_archived == 1,
        model.TodoistDate.parse(t.date_added).getOrElse(api.unexpected)
      )
    }
  }

  val fileAttachmentGen = for {
    name <- arbitrary[String]
    size <- Gen.posNum[Long]
    mime <- Gen.oneOf("application/pdf", "image/jpg", "image/png")
    url <- arbitrary[String]
    uploadState <- Gen.option(Gen.oneOf("completed", "pending"))
  } yield FileAttachment(name, size, mime, url, uploadState)

  test("FileAttachment") {
    forAll(fileAttachmentGen) { (t: FileAttachment) =>
      t.toModel shouldBe model.FileAttachment(
        t.file_name,
        t.file_size,
        t.file_type,
        t.file_url,
        model.UploadState.unsafe(t.upload_state)
      )
    }
  }

  val noteGen: Gen[Note] = for {
    id <- arbitrary[Int]
    uid <- arbitrary[Int]
    taskId <- arbitrary[Int]
    projectId <- arbitrary[Int]
    content <- arbitrary[String]
    file <- Gen.option(fileAttachmentGen)
    uids <- Gen.option(Gen.listOfN(7, arbitrary[Int]))
    is_deleted <- Gen.choose(0, 1)
    is_archived <- Gen.choose(0, 1)
    date_added <- arbitrary[Date].map(model.TodoistDate.format)
  } yield Note(id, uid, taskId, projectId, content, file, uids, is_deleted, is_archived, date_added)

  test("Note") {
    forAll(noteGen) { (t: Note) =>
      t.toModel shouldBe model.Note(
        t.id.noteId, t.posted_uid.userId, t.item_id.taskId, t.project_id.projectId, t.content,
        t.file_attachment.map(_.toModel),
        t.uids_to_notify.toList.flatten.map(_.userId),
        t.is_deleted == 1,
        t.is_archived == 1,
        model.TodoistDate.parse(t.posted).getOrElse(api.unexpected)
      )
    }
  }

  val filterGen: Gen[Filter] = for {
    id <- arbitrary[Int]
    name <- arbitrary[String]
    query <- arbitrary[String]
    color <- Gen.choose(0, 12)
    item_order <- arbitrary[Int]
    is_deleted <- Gen.choose(0, 1)
  } yield Filter(id, name, query, color, item_order, is_deleted)

  test("Filter") {
    forAll(filterGen) { (l: Filter) =>
      l.toModel shouldBe model.Filter(
        l.id.filterId, l.name, l.query, model.LabelColor.unsafeBy(l.color),
        l.item_order, l.is_deleted == 1
      )
    }
  }

  val reminderGen: Gen[Reminder] = for {
    id <- arbitrary[Int]
    uid <- arbitrary[Int]
    taskId <- arbitrary[Int]
    service <- Gen.oneOf("push", "mobile", "email")
    tpe <- Gen.oneOf("location", "absolute", "relative")
    date_str <- arbitrary[String]
    date_lang <- Gen.oneOf("en", "da", "pl", "zh", "ko", "de", "pt", "ja", "it", "fr", "sv", "ru", "es", "nl")
    due_date <- arbitrary[Date].map(model.TodoistDate.format)
    minute_offset <- Gen.posNum[Int]
    name <- arbitrary[String]
    lat <- arbitrary[Double]
    lon <- arbitrary[Double]
    trigger <- Gen.oneOf("on_enter", "on_leave")
    radius <- Gen.posNum[Int]
    is_deleted <- Gen.choose(0, 1)
  } yield tpe match {
    case "location" =>
      Reminder(id, uid, taskId, None, tpe, None, None, None, None, None, Some(name), Some(lat.toString), Some(lon.toString), Some(trigger), Some(radius), is_deleted)
    case "absolute" =>
      Reminder(id, uid, taskId, Some(service), tpe, Some(date_str), Some(date_lang), Some(due_date), None, None, None, None, None, None, None, is_deleted)
    case "relative" =>
      Reminder(id, uid, taskId, Some(service), tpe, Some(date_str), Some(date_lang), Some(due_date), Some(minute_offset), Some(minute_offset), None, None, None, None, None, is_deleted)
  }

  test("Reminder") {
    forAll(reminderGen) { (t: Reminder) =>
      t.toModel shouldBe (t.`type` match {
        case "location" =>
          model.LocationBasedReminder(
            t.id.reminderId,
            t.notify_uid.userId,
            t.item_id.taskId,
            t.name.get,
            t.loc_lat.get.toDouble,
            t.loc_long.get.toDouble,
            model.LocationBasedReminder.TriggerKind.unsafeBy(t.loc_trigger.get),
            t.radius.get,
            t.is_deleted == 1)
        case "absolute" =>
          model.AbsoluteTimeBasedReminder(
            t.id.reminderId,
            t.notify_uid.userId,
            t.item_id.taskId,
            model.ReminderService.unsafeBy(t.service.get),
            model.TaskDate(t.date_string, model.DateLanguage.unsafeBy(t.date_lang.get), model.TodoistDate.parse(t.due_date_utc.get).get),
            t.is_deleted == 1)
        case "relative" =>
          model.RelativeTimeBasedReminder(
            t.id.reminderId,
            t.notify_uid.userId,
            t.item_id.taskId,
            model.ReminderService.unsafeBy(t.service.get),
            model.TaskDate(t.date_string, model.DateLanguage.unsafeBy(t.date_lang.get), model.TodoistDate.parse(t.due_date_utc.get).get),
            t.minute_offset.orElse(t.mm_offset).get,
            t.is_deleted == 1)
      })
    }
  }

  val userGen: Gen[User] = for {
    id <- arbitrary[Int]
    email <- arbitrary[String]
    full_name <- arbitrary[String]
    inbox_project <- arbitrary[Int]
    timezone <- Gen.oneOf(TimeZone.getAvailableIDs.toList)
    hr <- Gen.choose(0, 23)
    start_page <- arbitrary[String]
    start_day <- Gen.choose(1, 7)
    next_week <- Gen.choose(1, 7)
    time_format <- Gen.choose(0, 1)
    date_format <- Gen.choose(0, 1)
    sort_order <- Gen.choose(0, 1)
    has_push <- arbitrary[Boolean]
    service <- Gen.option(Gen.oneOf("push", "mobile", "email"))
    auto_reminder <- Gen.option(Gen.posNum[Int])
    mobile <- arbitrary[Option[String]]
    mobile_host <- arbitrary[Option[String]]
    completed <- Gen.posNum[Int]
    completed_today <- Gen.posNum[Int]
    karma <- Gen.posNum[Double]
    premium_until <- Gen.option(arbitrary[Date].map(model.TodoistDate.format))
    is_biz_admin <- arbitrary[Boolean]
    biz_id <- arbitrary[Option[Int]]
    image_id <- arbitrary[Option[String]]
    beta <- Gen.choose(0, 1)
    is_dummy <- Gen.choose(0, 1)
    join_date <- arbitrary[Date].map(model.TodoistDate.format)
    theme <- Gen.choose(0, 9)
    a_sm <- arbitrary[Option[String]]
    a_m <- arbitrary[Option[String]]
    a_b <- arbitrary[Option[String]]
    a_640 <- arbitrary[Option[String]]
  } yield User(id, email, full_name, inbox_project, timezone, TimeZoneOffset(s"+$hr:00", hr, 0),
    start_page, start_day, next_week, time_format, date_format, sort_order, has_push, service, auto_reminder,
    mobile, mobile_host, completed, completed_today, karma, "up", premium_until.isDefined, premium_until,
    is_biz_admin, biz_id, image_id, beta, is_dummy, join_date, theme, a_sm, a_m, a_b, a_640)

  test("User") {
    forAll(userGen) { (t: User) =>
      t.toModel shouldBe model.User(
        t.id.userId,
        t.email,
        t.full_name,
        t.inbox_project.projectId,
        TimeZone.getTimeZone(t.timezone),
        t.start_page,
        model.DayOfWeek.unsafeBy(t.start_day),
        model.DayOfWeek.unsafeBy(t.next_week),
        model.TimeFormat.unsafeBy(t.time_format),
        model.DateFormat.unsafeBy(t.date_format),
        model.ProjectsSortOrder.unsafeBy(t.sort_order),
        t.has_push_reminders,
        t.default_reminder.map(model.ReminderService.unsafeBy),
        t.auto_reminder,
        t.mobile_number,
        t.mobile_host,
        t.completed_count,
        t.completed_today,
        t.karma,
        t.premium_until.map(d => model.TodoistDate.parse(d).getOrElse(api.unexpected)),
        t.is_biz_admin,
        t.business_account_id,
        t.beta == 1,
        t.is_dummy == 1,
        model.TodoistDate.parse(t.join_date).get,
        model.Theme.unsafeBy(t.theme),
        model.UserAvatars(
          t.avatar_small, t.avatar_medium, t.avatar_big, t.avatar_s640
        )
      )
    }
  }

  // command results

  val okGen: Gen[String] = Gen.const("ok")
  val errorGen: Gen[RawCommandError] = for {
    i <- arbitrary[Int]
    s <- arbitrary[String]
  } yield RawCommandError(i, s)

  implicit val rawItemStatusGen: Gen[RawItemStatus] = Gen.choose(0, 1).flatMap {
    case 0 => okGen.map(Coproduct[RawItemStatus](_))
    case 1 => errorGen.map(Coproduct[RawItemStatus](_))
  }

  val commandStatusGen: Gen[RawCommandStatus] = Gen.choose(0, 2).flatMap {
    case 0 => okGen.map(Coproduct[RawCommandStatus](_))
    case 1 => errorGen.map(Coproduct[RawCommandStatus](_))
    case 2 => for {
      n <- Gen.posNum[Int]
      st <- Gen.listOfN(n, rawItemStatusGen)
      key <- Gen.listOfN(n, Gen.posNum[Int].map(_.toString))
    } yield Coproduct[RawCommandStatus](key.zip(st).toMap)
  }

  test("CommandResult") {
    forAll(commandStatusGen) { (c: RawCommandStatus) =>
      c.toModel shouldBe (c match {
        case Inl(s) => model.CommandSuccess
        case Inr(Inl(err)) => model.CommandFailure(err.error_code, err.error)
        case Inr(Inr(Inl(map))) => model.MultiItemCommandStatus(
          map.map { case (k, v) => k.toInt -> v.toModel }
        )
        case Inr(Inr(Inr(cnil))) => cnil.impossible
      })
    }
  }

  val commandAndResultGen: Gen[(model.TempIdCommand[_], RawCommandResult)] = for {
    uuid <- Gen.uuid
    tempId <- Gen.uuid
    status <- rawItemStatusGen
    id <- Gen.posNum[Int]
  } yield (
    model.AddProject("a", uuid = uuid, tempId = tempId.projectId),
    RawCommandResult(Map(uuid.toString -> status.embed[RawCommandStatus]), Some(Map(tempId.toString -> id)))
    )

  test("TempIdCommandResult") {
    forAll(commandAndResultGen) { case (c: model.TempIdCommand[_], r: RawCommandResult) =>
      FromDTO.tempIdCommandStatusFromDTO(c, r) shouldBe (r.SyncStatus(c.uuid.toString) match {
        case Inl(_) =>
          Some(TempIdCommandResult(c.uuid,
            TempIdSuccess(c.tempId,
              r.TempIdMapping.get(c.tempId.toString)
            )
          ))
        case Inr(Inl(e)) =>
          Some(TempIdCommandResult(c.uuid, TempIdFailure(e.error_code, e.error)))
        case Inr(Inr(Inl(multipleCommandStatus))) => api.unexpected
        case Inr(Inr(Inr(cNil))) => cNil.impossible
      })
    }
  }

  test("Functor FromDTO") {
    forAll(Gen.listOf(labelGen)) { (l: List[Label]) =>
      import cats.std.list._
      l.toModel shouldBe l.map(_.toModel)
    }
  }
}

