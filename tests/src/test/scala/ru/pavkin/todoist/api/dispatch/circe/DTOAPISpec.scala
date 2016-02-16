package ru.pavkin.todoist.api.dispatch.circe

import io.circe.Json
import ru.pavkin.todoist.api.circe.CirceDecoder
import ru.pavkin.todoist.api.core.APISuiteSpec
import ru.pavkin.todoist.api.dispatch.impl.circe.model.DispatchModelAPI

class DTOAPISpec
  extends APISuiteSpec[DispatchModelAPI.Result, CirceDecoder.Result, Json]("Dispatch Circe DTO API")
    with DTOAPI
