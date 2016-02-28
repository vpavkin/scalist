package ru.pavkin.todoist.api.dispatch.circe

import io.circe.Json
import org.scalatest.FunSuite
import org.scalatest.prop.Checkers
import ru.pavkin.todoist.api.circe.CirceDecoder
import ru.pavkin.todoist.api.core.model._
import ru.pavkin.todoist.api.core.query.{MultipleQueryDefinition, SingleQueryDefinition}
import ru.pavkin.todoist.api.dispatch.impl.circe.DispatchAPI
import shapeless.test._
import shapeless.{::, HNil}

import scala.concurrent.ExecutionContext.Implicits.global

class CirceModelAPISpec
  extends FunSuite with Checkers with CirceModelAPISuite {

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
    typed[MultipleQueryDefinition[DispatchAPI.Result, CirceDecoder.Result, Tasks :: Labels :: Projects :: HNil, Json]](
      api.getAll[Tasks :: Labels :: Projects :: HNil]
    )
    typed[MultipleQueryDefinition[DispatchAPI.Result, CirceDecoder.Result, Labels :: Projects :: HNil, Json]](
      api.getAll[Labels :: Projects :: HNil]
    )
    typed[MultipleQueryDefinition[DispatchAPI.Result, CirceDecoder.Result, Labels :: Projects :: HNil, Json]](
      api.getAll[Projects :: HNil].and[Labels]
    )

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

}
