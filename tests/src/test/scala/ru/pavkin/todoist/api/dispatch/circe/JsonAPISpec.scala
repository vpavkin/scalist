package ru.pavkin.todoist.api.dispatch.circe

import io.circe.Json
import ru.pavkin.todoist.api.circe.CirceDecoder
import ru.pavkin.todoist.api.core.FutureBasedAPISuiteSpec
import ru.pavkin.todoist.api.core.command.{SingleCommandDefinition, MultipleCommandDefinition}
import ru.pavkin.todoist.api.dispatch.impl.circe.DispatchAPI
import shapeless.{HNil, ::}
import shapeless.test.typed

import scala.concurrent.ExecutionContext.Implicits.global

class JsonAPISpec
  extends FutureBasedAPISuiteSpec[DispatchAPI.Result, CirceDecoder.Result, Json]("Dispatch Circe Json API")
    with JsonAPI {

  test(s"ololo") {
    val api = todoist.withToken("token")
    typed[SingleCommandDefinition[DispatchAPI.Result, CirceDecoder.Result, Json, Json, Json]](
      api.perform(Json.obj())
    )
    typed[MultipleCommandDefinition[DispatchAPI.Result,
      CirceDecoder.Result,
      Json :: Json :: HNil,
      Json :: Json :: HNil,
      Json]](
      api.perform(Json.obj()).and(Json.array())
    )

    typed[MultipleCommandDefinition[DispatchAPI.Result,
      CirceDecoder.Result,
      Json :: Json :: Json :: HNil,
      Json :: Json :: Json :: HNil,
      Json]](
      api.performAll(Json.obj() :: Json.array() :: Json.array() :: HNil)
    )
  }

}
