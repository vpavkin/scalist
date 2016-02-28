package ru.pavkin.todoist.api.core

import java.text.SimpleDateFormat
import java.util.{UUID, Date}

import org.scalacheck.Arbitrary._
import org.scalacheck.Gen
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalatest.{FunSuite, Matchers}
import ru.pavkin.todoist.api.core.ToDTO.syntax._
import ru.pavkin.todoist.api.core.model._
import tags.syntax._

class ToDTOSpec extends FunSuite with Matchers with GeneratorDrivenPropertyChecks {

  val addProjectGen: Gen[AddProject] = for {
    name <- arbitrary[String]
    color <- Gen.option(Gen.choose(0, 21).map(ProjectColor.unsafeBy))
    indent <- Gen.option(Gen.choose(1, 4).map(Indent.unsafeBy))
    order <- Gen.option(arbitrary[Int])
  } yield AddProject(name, color, indent, order)

  test("AddProject") {
    forAll(addProjectGen) { (p: AddProject) =>
      p.toDTO shouldBe dto.AddProject(
        p.name,
        p.color.map(_.code),
        p.indent.map(_.code),
        p.order
      )
    }
  }

  val addLabelGen: Gen[AddLabel] = for {
    name <- arbitrary[String]
    color <- Gen.option(Gen.choose(0, 12).map(LabelColor.unsafeBy))
    order <- Gen.option(arbitrary[Int])
  } yield AddLabel(name, color, order)

  test("AddLabel") {
    forAll(addLabelGen) { (p: AddLabel) =>
      p.toDTO shouldBe dto.AddLabel(
        p.name,
        p.color.map(_.code),
        p.order
      )
    }
  }

  val taskDateGen: Gen[TaskDate] = for {
    str <- arbitrary[Option[String]]
    lang <- Gen.oneOf(DateLanguage.en,
      DateLanguage.da,
      DateLanguage.pl,
      DateLanguage.zh,
      DateLanguage.ko,
      DateLanguage.de,
      DateLanguage.pt,
      DateLanguage.ja,
      DateLanguage.it,
      DateLanguage.fr,
      DateLanguage.sv,
      DateLanguage.ru,
      DateLanguage.es,
      DateLanguage.nl
    )
    date <- arbitrary[Date]
  } yield TaskDate(str, lang, date)

  def addTaskGen[T: IsResourceId](gen: Gen[T]): Gen[AddTask[T]] = for {
    content <- arbitrary[String]
    projectId <- gen.map(_.projectId)
    date <- Gen.option(taskDateGen)
    priority <- Gen.option(Gen.oneOf(Priority.level1, Priority.level2, Priority.level3, Priority.level4))
    indent <- Gen.option(Gen.oneOf(Indent.level1, Indent.level2, Indent.level3, Indent.level4))
    order <- Gen.option(arbitrary[Int])
    dayOrder <- Gen.option(arbitrary[Int])
    isCollapsed <- arbitrary[Option[Boolean]]
    labels <- arbitrary[List[Int]].map(_.map(_.labelId))
    assignedBy <- arbitrary[Option[Int]].map(_.map(_.userId))
    responsible <- arbitrary[Option[Int]].map(_.map(_.userId))
  } yield AddTask(
    content, projectId, date, priority, indent, order, dayOrder, isCollapsed, labels, assignedBy, responsible
  )

  test("AddTask") {
    forAll(addTaskGen(Gen.uuid)) { (p: AddTask[UUID]) =>
      p.toDTO shouldBe dto.AddTask(
        p.content,
        p.projectId: UUID,
        p.date.flatMap(_.text),
        p.date.map(_.language.code),
        p.date.map(_.dueDateUTC).map(TodoistDate.format),
        p.priority.map(_.level),
        p.indent.map(_.code),
        p.order,
        p.dayOrder,
        p.isCollapsed.map(b => if (b) 1 else 0),
        p.labels,
        p.assignedBy,
        p.responsible
      )
    }
  }

  val addTaskToInboxGen: Gen[AddTaskToInbox] = addTaskGen(Gen.uuid).map(p => AddTaskToInbox(
    p.content,
    p.date,
    p.priority,
    p.indent,
    p.order,
    p.dayOrder,
    p.isCollapsed,
    p.labels
  ))

  test("AddTaskToInbox") {
    forAll(addTaskToInboxGen) { (p: AddTaskToInbox) =>
      p.toDTO shouldBe dto.AddTaskToInbox(
        p.content,
        p.date.flatMap(_.text),
        p.date.map(_.language.code),
        p.date.map(_.dueDateUTC).map(TodoistDate.format),
        p.priority.map(_.level),
        p.indent.map(_.code),
        p.order,
        p.dayOrder,
        p.isCollapsed.map(b => if (b) 1 else 0),
        p.labels
      )
    }
  }

  def updateProjectGen[T: IsResourceId](gen: Gen[T]): Gen[UpdateProject[T]] = for {
    o <- arbitrary[Option[Int]]
    p <- addProjectGen
    collapsed <- arbitrary[Option[Boolean]]
    id <- gen
  } yield UpdateProject[T](
    id.projectId, o.map(_ => p.name), p.color, p.indent, p.order, collapsed
  )

  test("UpdateProject") {
    forAll(updateProjectGen(Gen.uuid)) { (p: UpdateProject[UUID]) =>
      p.toDTO shouldBe dto.UpdateProject(
        p.id: UUID,
        p.name,
        p.color.map(_.code),
        p.indent.map(_.code),
        p.order,
        p.isCollapsed.map(b => if (b) 1 else 0)
      )
    }
  }

  def updateLabelGen[T: IsResourceId](gen: Gen[T]): Gen[UpdateLabel[T]] = for {
    o <- arbitrary[Option[Int]]
    p <- addLabelGen
    id <- gen
  } yield UpdateLabel[T](
    id.labelId, o.map(_ => p.name), p.color, p.order
  )

  test("UpdateLabel") {
    forAll(updateLabelGen(arbitrary[Int])) { (p: UpdateLabel[Int]) =>
      p.toDTO shouldBe dto.UpdateLabel(
        p.id: Int,
        p.name,
        p.color.map(_.code),
        p.order
      )
    }
  }

  def updateTaskGen[T: IsResourceId](gen: Gen[T]): Gen[UpdateTask[T]] = for {
    o <- arbitrary[Option[Int]]
    p <- addTaskGen(Gen.uuid)
    id <- gen
  } yield UpdateTask[T](
    id.taskId, o.map(_ => p.content), p.date, p.priority, p.indent,
    p.order, p.dayOrder, p.isCollapsed, p.labels, p.assignedBy, p.responsible
  )

  test("UpdateTask") {
    forAll(updateTaskGen(arbitrary[Int])) { (p: UpdateTask[Int]) =>
      p.toDTO shouldBe dto.UpdateTask(
        p.id: Int,
        p.content,
        p.date.flatMap(_.text),
        p.date.map(_.language.code),
        p.date.map(_.dueDateUTC).map(TodoistDate.format),
        p.priority.map(_.level),
        p.indent.map(_.code),
        p.order,
        p.dayOrder,
        p.isCollapsed.map(b => if (b) 1 else 0),
        p.labels,
        p.assignedBy,
        p.responsible
      )
    }
  }
}

