package ru.pavkin.todoist.api.core

import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalatest.{FunSuite, Matchers}
import ru.pavkin.todoist.api
import ru.pavkin.todoist.api.core.FromDTO.syntax._
import ru.pavkin.todoist.api.core.dto._
import ru.pavkin.todoist.api.core.model.{TempIdCommandResult, TempIdFailure, TempIdSuccess}
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

