package ru.pavkin.todoist.api.dispatch

import cats.Id
import _root_.dispatch.Req
import ru.pavkin.todoist.api._
import ru.pavkin.todoist.api.core._
import ru.pavkin.todoist.api.dispatch.core.DispatchAuthorizedRequestFactory
import ru.pavkin.todoist.api.dispatch.impl.string.DispatchStringRequestExecutor.Result
import ru.pavkin.todoist.api.dispatch.impl.string.{DispatchStringAPI, DispatchStringRequestExecutor}
import ru.pavkin.todoist.api.parser.SingleResourceParser

object string {

  type Projects = tags.Projects
  type Labels = tags.Labels

  implicit val projects = IsResource[Projects](Vector("projects"))
  implicit val labels = IsResource[Labels](Vector("labels"))

  implicit def dummySingleParser[T]: SingleResourceParser.Aux[Id, String, T] =
    SingleResourceParser.using[Id, String, T](_ => unexpected[T])

  val todoist = new UnauthorizedAPI[DispatchStringRequestExecutor.Result, Id, String] {
    private lazy val executor: RequestExecutor.Aux[Req, DispatchStringRequestExecutor.Result, String] = new DispatchStringRequestExecutor

    def authorize(token: Token): API[Result, Id, String] =
      new DispatchStringAPI(
        new DispatchAuthorizedRequestFactory(token),
        executor
      )
  }
}
