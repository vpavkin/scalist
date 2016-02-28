package ru.pavkin.todoist.api.core

import java.util.Date

import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalatest.{FunSuite, Matchers}
import ru.pavkin.todoist.api
import ru.pavkin.todoist.api.core.FromDTO.syntax._
import ru.pavkin.todoist.api.core.dto.Label
import ru.pavkin.todoist.api.core.dto.Project
import ru.pavkin.todoist.api.core.dto.Task
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

