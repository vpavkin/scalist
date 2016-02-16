package ru.pavkin.todoist.api.dispatch.circe

import cats._
import io.circe.Json
import ru.pavkin.todoist.api.core.APISuiteSpec
import ru.pavkin.todoist.api.dispatch.impl.circe.json.DispatchJsonRequestExecutor

class JsonAPISpec
  extends APISuiteSpec[DispatchJsonRequestExecutor.Result, Id, Json]("Dispatch Circe Json API")
    with JsonAPI
