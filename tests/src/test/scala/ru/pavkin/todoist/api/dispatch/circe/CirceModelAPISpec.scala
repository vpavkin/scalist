package ru.pavkin.todoist.api.dispatch.circe

import java.util.UUID

import io.circe.Json
import ru.pavkin.todoist.api.circe.CirceDecoder
import ru.pavkin.todoist.api.core.FutureBasedAPISuiteSpec
import ru.pavkin.todoist.api.core.dto.{RawCommandResult, AllResources}
import ru.pavkin.todoist.api.core.model._
import ru.pavkin.todoist.api.dispatch.impl.circe.DispatchAPI
import shapeless.HNil

import scala.concurrent.ExecutionContext.Implicits.global

class CirceModelAPISpec
  extends FutureBasedAPISuiteSpec[DispatchAPI.Result, CirceDecoder.Result, Json, AllResources, RawCommandResult](
    "Dispatch Circe API"
  ) with CirceModelAPISuite {

  import syntax._

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
