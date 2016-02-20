package ru.pavkin.todoist.api.dispatch.circe

import io.circe.Json
import ru.pavkin.todoist.api.circe.CirceDecoder
import ru.pavkin.todoist.api.core.FutureBasedAPISuiteSpec
import ru.pavkin.todoist.api.core.dto.{CommandResult, AllResources}
import ru.pavkin.todoist.api.dispatch.impl.circe.DispatchAPI

import scala.concurrent.ExecutionContext.Implicits.global

class CirceDTOAPISpec
  extends FutureBasedAPISuiteSpec[DispatchAPI.Result, CirceDecoder.Result, Json, AllResources, CommandResult](
    "Dispatch Circe DTO API"
  ) with CirceDTOAPISuite
