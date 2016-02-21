package ru.pavkin.todoist.api.dispatch.circe

import io.circe.Json
import ru.pavkin.todoist.api.circe.CirceDecoder
import ru.pavkin.todoist.api.dispatch.impl.circe.DispatchAPI
import ru.pavkin.todoist.api.suite.ModelAPISuite

trait CirceModelAPISuite
  extends CirceAPISuite
    with ModelAPISuite[DispatchAPI.Result, CirceDecoder.Result, Json]
