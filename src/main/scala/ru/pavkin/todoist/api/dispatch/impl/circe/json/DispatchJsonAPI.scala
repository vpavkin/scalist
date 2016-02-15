package ru.pavkin.todoist.api.dispatch.impl.circe.json

import dispatch.Req
import io.circe.Json
import ru.pavkin.todoist.api.core.plain.PlainAPI
import ru.pavkin.todoist.api.core.{AuthorizedRequestFactory, RequestExecutor}

class DispatchJsonAPI(override val requestFactory: AuthorizedRequestFactory[Vector[String], Req],
                      override val executor: RequestExecutor.Aux[Req, DispatchJsonRequestExecutor.Result, Json])
  extends PlainAPI[DispatchJsonRequestExecutor.Result, Req, Json]
