package ru.pavkin.todoist.api.dispatch.circe

import io.circe.Json
import org.scalacheck.{Arbitrary, Gen}
import org.scalacheck.Arbitrary._
import org.scalatest.{Matchers, FunSuite}
import org.scalatest.prop.{GeneratorDrivenPropertyChecks, Checkers}
import ru.pavkin.todoist.api.circe.CirceDecoder
import ru.pavkin.todoist.api.core.model._
import ru.pavkin.todoist.api.core.query.{MultipleQueryDefinition, SingleQueryDefinition}
import ru.pavkin.todoist.api.dispatch.impl.circe.DispatchAPI
import shapeless.test._
import shapeless.{::, HNil}

import scala.concurrent.ExecutionContext.Implicits.global

class CirceModelAPISpec
  extends FunSuite with GeneratorDrivenPropertyChecks with Matchers with CirceModelAPISuite {

  import syntax._

  test("Circe query test suite") {
    val api = todoist.withToken("token")
    typed[SingleQueryDefinition[DispatchAPI.Result, CirceDecoder.Result, Projects, Json]](
      api.get[Projects]
    )
    typed[SingleQueryDefinition[DispatchAPI.Result, CirceDecoder.Result, Tasks, Json]](
      api.get[Tasks]
    )
    typed[MultipleQueryDefinition[DispatchAPI.Result, CirceDecoder.Result, Labels :: Projects :: HNil, Json]](
      api.get[Projects].and[Labels]
    )
    typed[MultipleQueryDefinition[DispatchAPI.Result, CirceDecoder.Result, Notes :: Tasks :: Labels :: Projects :: HNil, Json]](
      api.get[Projects].and[Labels].and[Tasks].and[Notes]
    )
    typed[MultipleQueryDefinition[DispatchAPI.Result, CirceDecoder.Result, Tasks :: Labels :: Projects :: HNil, Json]](
      api.getAll[Tasks :: Labels :: Projects :: HNil]
    )
    typed[MultipleQueryDefinition[DispatchAPI.Result, CirceDecoder.Result, Labels :: Projects :: HNil, Json]](
      api.getAll[Labels :: Projects :: HNil]
    )
    typed[MultipleQueryDefinition[DispatchAPI.Result, CirceDecoder.Result, Labels :: Projects :: HNil, Json]](
      api.getAll[Projects :: HNil].and[Labels]
    )

    api.getAll[Tasks :: Labels :: Filters :: HNil]
    api.getAll[Filters :: Notes :: Tasks :: Labels :: Projects :: HNil]
    api.getAll[All]
    api.get[Projects].and[Labels].and[Tasks].and[Notes].and[Filters]

    illTyped("""api.get[String]""")
    illTyped("""api.get""")
    illTyped("""api.get[Projects].and[Int]""")
    illTyped("""api.get[Projects].and[Projects]""")
    illTyped("""api.get[Projects].and[Labels].and[Projects]""")
    illTyped("""api.getAll[Labels :: Projects :: HNil].and[Labels]""")
    illTyped("""api.getAll[Labels :: Projects :: Labels :: HNil]""")
  }

  test("Dispatch Circe API command syntax test suite") {
    val api = todoist.withToken("token")

    api.perform(AddProject("Learn Scalist"))

    api.perform(AddProject("Learn Scalist"))
      .and(AddLabel("label"))

    api.performAll(
      AddProject("Learn Scalist") :+
        AddProject("Try Scalist") :+
        AddProject("Add Scalist to my project")
    ).and(AddLabel("label"))

    api.performAll(AddProject("Learn Scalist") ::
      AddProject("Try Scalist") ::
      AddProject("Add Scalist to my project") :: HNil)

    api.performAll(AddProject("Learn Scalist").andForIt(AddTask("task", _)))

    api.performAll(
      AddProject("Learn Scalist").andForItAll(id =>
        AddTask("task1", id) :+ AddTask("task2", id)
      ) :+ AddLabel("label")
    )
  }

  test("Dispatch Circe API command support test suite") {
    val api = todoist.withToken("token")
    api.perform(AddProject("A"))
    api.perform(AddLabel("A"))
    api.perform(AddTask("A", 1.projectId))
    api.perform(AddTaskToInbox("A"))
    api.perform(UpdateProject(1.projectId, Some("A")))
    api.perform(UpdateTask(1.taskId, Some("A")))
    api.perform(UpdateLabel(1.labelId, Some("A")))
    api.perform(DeleteProjects(List(1, 2).projectIds))
    api.perform(ArchiveProjects(List(1, 2).projectIds))
    api.perform(UnarchiveProjects(List(1, 2).projectIds))
  }

  test("Dispatch Circe OAuth API test suite") {
    implicit val genTokenScope: Gen[TokenScope] = Gen.choose(0, 4).flatMap {
      case 0 => TokenScope.AddTasks
      case 1 => TokenScope.Read
      case 2 => TokenScope.ReadWrite
      case 3 => TokenScope.Delete
      case 4 => TokenScope.DeleteProjects
    }
    implicit val arbTokenScope = Arbitrary(genTokenScope)

    val oAuthStep1Gen = for {
      scope <- arbitrary[Set[TokenScope]]
      clientId <- arbitrary[String]
      state <- arbitrary[String]
    } yield (clientId, scope, state)

    forAll(oAuthStep1Gen) { tuple =>
      todoist.auth.oAuthStep1URL(tuple._1, tuple._2, tuple._3) shouldBe
        s"https://todoist.com/oauth/authorize?" +
          s"client_id=${tuple._1}&" +
          s"state=${tuple._3}&" +
          s"scope=${tuple._2.map(_.name).mkString(",")}"
    }

    todoist.auth.oAuthStep3(TokenExchange("", "", ""))
  }


  // todo: extract generators and use them here
  test("Query result syntax test") {
    val p = List.empty[Project]
    val rp = p :: HNil
    rp.projects shouldBe p
    illTyped("""rp.tasks""")
    illTyped("""rp.labels""")
    illTyped("""rp.notes""")

    val all = p ::
      List.empty[Label] ::
      List.empty[Task] ::
      List.empty[Note] ::
      List.empty[Filter] ::
      List.empty[Reminder] ::
      HNil
    all.projects shouldBe p
    all.labels shouldBe List.empty[Label]
    all.tasks shouldBe List.empty[Task]
    all.notes shouldBe List.empty[Note]
    all.filters shouldBe List.empty[Filter]
    all.reminders shouldBe List.empty[Reminder]
  }
}
