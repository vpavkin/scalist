package ru.pavkin.todoist.api.dispatch.circe

import java.util.UUID

import io.circe.Json
import ru.pavkin.todoist.api.circe.CirceDecoder
import ru.pavkin.todoist.api.core.FutureBasedAPISuiteSpec
import ru.pavkin.todoist.api.core.command.{MultipleCommandDefinition, SingleCommandDefinition}
import ru.pavkin.todoist.api.core.dto._
import ru.pavkin.todoist.api.dispatch.impl.circe.DispatchAPI
import shapeless.{HNil, ::}
import shapeless.test.typed
import scala.concurrent.ExecutionContext.Implicits.global

class CirceDTOAPISpec
  extends FutureBasedAPISuiteSpec[DispatchAPI.Result, CirceDecoder.Result, Json, AllResources, RawCommandResult](
    "Dispatch Circe DTO API"
  ) with CirceDTOAPISuite {

  test("Dispatch Circe DTO API command test suite") {
    val api = todoist.withToken("token")

    typed[SingleCommandDefinition[DispatchAPI.Result, CirceDecoder.Result, RawCommand[AddTask], SingleCommandResult, Json]](
      api.perform(RawCommand("item_add", UUID.randomUUID(), AddTask("Some name")))
    )

    typed[SingleCommandDefinition[DispatchAPI.Result, CirceDecoder.Result, RawCommandWithTempId[AddTask], SingleCommandResultWithTempId, Json]](
      api.perform(RawCommandWithTempId("item_add", UUID.randomUUID(), AddTask("Some name"), UUID.randomUUID()))
    )

    typed[MultipleCommandDefinition[
      DispatchAPI.Result,
      CirceDecoder.Result,
      RawCommandWithTempId[AddTask] :: RawCommand[AddProject] :: HNil,
      SingleCommandResultWithTempId :: SingleCommandResult :: HNil,
      Json]](
      api.perform(RawCommand("project_add", UUID.randomUUID(), AddProject("Some name")))
        .and(RawCommandWithTempId("item_add", UUID.randomUUID(), AddTask("Some name"), UUID.randomUUID()))
    )
  }

}
