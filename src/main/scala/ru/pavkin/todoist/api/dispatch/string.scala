package ru.pavkin.todoist.api.dispatch

import dispatch.Req
import ru.pavkin.todoist.api.Token
import ru.pavkin.todoist.api.core.{API, IsResource, RequestExecutor, UnauthorizedAPI}
import ru.pavkin.todoist.api.dispatch.core.DispatchAuthorizedRequestFactory
import ru.pavkin.todoist.api.dispatch.impl.string.DispatchStringRequestExecutor.Result
import ru.pavkin.todoist.api.dispatch.impl.string.{DispatchStringAPI, DispatchStringRequestExecutor}

object string {

  type Projects = ru.pavkin.todoist.api.core.ReadResourceType.Projects
  type Labels = ru.pavkin.todoist.api.core.ReadResourceType.Labels

  implicit val projects = IsResource[Projects, String](Vector("projects"))
  implicit val labels = IsResource[Labels, String](Vector("labels"))

  val todoist = new UnauthorizedAPI[DispatchStringRequestExecutor.Result] {
    private lazy val executor: RequestExecutor.Aux[Req, DispatchStringRequestExecutor.Result, String] = new DispatchStringRequestExecutor

    def authorize(token: Token): API[Result] =
      new DispatchStringAPI(
        new DispatchAuthorizedRequestFactory(token),
        executor
      )
  }
}
