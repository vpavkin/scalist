package ru.pavkin.todoist.api.dispatch.circe

import io.circe.Json
import ru.pavkin.todoist.api.circe.CirceDecoder
import ru.pavkin.todoist.api.dispatch.impl.circe.DispatchAPI
import ru.pavkin.todoist.api.suite.DTOAPISuite

trait CirceDTOAPISuite
  extends CirceAPISuite
    with DTOAPISuite[DispatchAPI.Result, CirceDecoder.Result, Json]
