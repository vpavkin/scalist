package ru.pavkin.todoist.api.dispatch.circe

import cats.Id
import io.circe.Json
import ru.pavkin.todoist.api.core.FutureBasedAPISuiteSpec
import ru.pavkin.todoist.api.dispatch.impl.circe.json.DispatchJsonRequestExecutor
import scala.concurrent.ExecutionContext.Implicits.global

class JsonAPISpec
  extends FutureBasedAPISuiteSpec[DispatchJsonRequestExecutor.Result, Id, Json]("Dispatch Circe Json API")
    with JsonAPI
