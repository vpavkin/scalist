package ru.pavkin.todoist.api.dispatch

import cats.Id
import dispatch.Req
import ru.pavkin.todoist.api._
import ru.pavkin.todoist.api.core._
import ru.pavkin.todoist.api.core.plain.PlainAPISuite
import ru.pavkin.todoist.api.dispatch.core.DispatchAuthorizedRequestFactory
import ru.pavkin.todoist.api.dispatch.impl.string.{DispatchStringAPI, DispatchStringRequestExecutor}

object string extends PlainAPISuite[String] {

  val todoist = new UnauthorizedAPI[DispatchStringRequestExecutor.Result, Id, String] {
    private lazy val executor: RequestExecutor.Aux[Req, DispatchStringRequestExecutor.Result, String] =
      new DispatchStringRequestExecutor

    def authorize(token: Token): API[DispatchStringRequestExecutor.Result, Id, String] =
      new DispatchStringAPI(
        new DispatchAuthorizedRequestFactory(token),
        executor
      )
  }
}
