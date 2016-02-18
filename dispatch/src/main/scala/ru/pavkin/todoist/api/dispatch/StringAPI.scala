package ru.pavkin.todoist.api.dispatch

import cats.Id
import dispatch.Req
import ru.pavkin.todoist.api.Token
import ru.pavkin.todoist.api.core.plain.PlainAPISuite
import ru.pavkin.todoist.api.core.{FutureBasedAPISuite, UnauthorizedAPI, RequestExecutor, API}
import ru.pavkin.todoist.api.dispatch.core.DispatchAuthorizedRequestFactory
import ru.pavkin.todoist.api.dispatch.impl.string.{DispatchStringRequestExecutor, DispatchStringAPI}

import scala.concurrent.ExecutionContext

trait StringAPI
  extends PlainAPISuite[String, DispatchStringRequestExecutor.Result]
    with FutureBasedAPISuite[DispatchStringRequestExecutor.Result, Id, String] {

  override def todoist(implicit ec: ExecutionContext) =
    new UnauthorizedAPI[DispatchStringRequestExecutor.Result, Id, String] {
      private lazy val executor: RequestExecutor.Aux[Req, DispatchStringRequestExecutor.Result, String] =
        new DispatchStringRequestExecutor

      def withToken(token: Token): API[DispatchStringRequestExecutor.Result, Id, String] =
        new DispatchStringAPI(
          new DispatchAuthorizedRequestFactory(token),
          executor
        )
    }
}
