package ru.pavkin.todoist.api.dispatch

import cats.Id
import dispatch.Req
import io.circe.Json
import ru.pavkin.todoist.api.Token
import ru.pavkin.todoist.api.core._
import ru.pavkin.todoist.api.core.plain.PlainAPISuite
import ru.pavkin.todoist.api.dispatch.core.DispatchAuthorizedRequestFactory
import ru.pavkin.todoist.api.dispatch.impl.circe.json.{DispatchJsonAPI, DispatchJsonRequestExecutor}

object json extends PlainAPISuite[Json]{

  val todoist = new UnauthorizedAPI[DispatchJsonRequestExecutor.Result, Id, Json] {
    private lazy val executor: RequestExecutor.Aux[Req, DispatchJsonRequestExecutor.Result, Json] = new DispatchJsonRequestExecutor

    def authorize(token: Token): API[DispatchJsonRequestExecutor.Result, Id, Json] =
      new DispatchJsonAPI(
        new DispatchAuthorizedRequestFactory(token),
        executor
      )
  }
}
