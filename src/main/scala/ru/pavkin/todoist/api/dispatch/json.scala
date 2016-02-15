package ru.pavkin.todoist.api.dispatch

import cats.Id
import dispatch.Req
import io.circe.Json
import ru.pavkin.todoist.api.Token
import ru.pavkin.todoist.api.core._
import ru.pavkin.todoist.api.unexpected
import ru.pavkin.todoist.api.dispatch.core.DispatchAuthorizedRequestFactory
import ru.pavkin.todoist.api.dispatch.impl.circe.json.{DispatchJsonAPI, DispatchJsonRequestExecutor}
import ru.pavkin.todoist.api.parser.SingleResourceParser

object json {

  type Projects = tags.Projects
  type Labels = tags.Labels

  implicit val projects = IsResource[Projects](Vector("projects"))
  implicit val labels = IsResource[Labels](Vector("labels"))

  implicit def dummySingleParser[T]: SingleResourceParser.Aux[Id, Json, T] =
    SingleResourceParser.using[Id, Json, T](_ => unexpected[T])

  val todoist = new UnauthorizedAPI[DispatchJsonRequestExecutor.Result, Id, Json] {
    private lazy val executor: RequestExecutor.Aux[Req, DispatchJsonRequestExecutor.Result, Json] = new DispatchJsonRequestExecutor

    def authorize(token: Token): API[DispatchJsonRequestExecutor.Result, Id, Json] =
      new DispatchJsonAPI(
        new DispatchAuthorizedRequestFactory(token),
        executor
      )
  }
}
