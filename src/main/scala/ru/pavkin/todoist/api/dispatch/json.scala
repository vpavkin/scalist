package ru.pavkin.todoist.api.dispatch

import dispatch.Req
import io.circe.Json
import ru.pavkin.todoist.api.Token
import ru.pavkin.todoist.api.core.{API, UnauthorizedAPI, IsResource, RequestExecutor}
import ru.pavkin.todoist.api.dispatch.core.DispatchAuthorizedRequestFactory
import ru.pavkin.todoist.api.dispatch.impl.circe.json.{DispatchJsonAPI, DispatchJsonRequestExecutor}

object json {

  type Projects = ru.pavkin.todoist.api.core.ReadResourceType.Projects
  type Labels = ru.pavkin.todoist.api.core.ReadResourceType.Labels

  implicit val projects = IsResource[Projects, Json](Vector("projects"))
  implicit val labels = IsResource[Labels, Json](Vector("labels"))

  val todoist = new UnauthorizedAPI[DispatchJsonRequestExecutor.Result] {
    private lazy val executor: RequestExecutor.Aux[Req, DispatchJsonRequestExecutor.Result, Json] = new DispatchJsonRequestExecutor

    def authorize(token: Token): API[DispatchJsonRequestExecutor.Result] =
      new DispatchJsonAPI(
        new DispatchAuthorizedRequestFactory(token),
        executor
      )
  }
}
