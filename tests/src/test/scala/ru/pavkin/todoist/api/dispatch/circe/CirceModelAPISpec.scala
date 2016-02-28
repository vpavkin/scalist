package ru.pavkin.todoist.api.dispatch.circe

import io.circe.Json
import org.scalatest.{Matchers, FunSuite}
import org.scalatest.prop.Checkers
import ru.pavkin.todoist.api.circe.CirceDecoder
import ru.pavkin.todoist.api.core.model._
import ru.pavkin.todoist.api.core.query.{MultipleQueryDefinition, SingleQueryDefinition}
import ru.pavkin.todoist.api.dispatch.impl.circe.DispatchAPI
import shapeless.test._
import shapeless.{::, HNil}

import scala.concurrent.ExecutionContext.Implicits.global

class CirceModelAPISpec
  extends FunSuite with Checkers with Matchers with CirceModelAPISuite {

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
    api.get[Projects].and[Labels].and[Tasks].and[Notes].and[Filters]

    illTyped("""api.get[String]""")
    illTyped("""api.get""")
    illTyped("""api.get[Projects].and[Int]""")
    illTyped("""api.get[Projects].and[Projects]""")
    illTyped("""api.get[Projects].and[Labels].and[Projects]""")
    illTyped("""api.getAll[Labels :: Projects :: HNil].and[Labels]""")
    illTyped("""api.getAll[Labels :: Projects :: Labels :: HNil]""")
  }

  test("Dispatch Circe API command test suite") {
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

  // todo: extract generators and use them here
  test("Query result syntax test") {
    val p = List.empty[Project]
    val rp = p :: HNil
    rp.projects shouldBe p
    illTyped("""rp.tasks""")
    illTyped("""rp.labels""")
    illTyped("""rp.notes""")

    val all = p :: List.empty[Label] :: List.empty[Task] :: List.empty[Note] :: HNil
    all.projects shouldBe p
    all.labels shouldBe List.empty[Label]
    all.tasks shouldBe List.empty[Task]
    all.notes shouldBe List.empty[Note]
  }
}
