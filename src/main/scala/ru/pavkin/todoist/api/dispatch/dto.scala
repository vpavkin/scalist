package ru.pavkin.todoist.api.dispatch

import dispatch.Req
import io.circe.Json
import ru.pavkin.todoist.api.Token
import ru.pavkin.todoist.api.circe.CirceDecoder
import ru.pavkin.todoist.api.circe.decoders.DTODecoders
import ru.pavkin.todoist.api.core._
import ru.pavkin.todoist.api.core.dto.{Label, Project}
import ru.pavkin.todoist.api.core.parser.ParserAPISuite
import ru.pavkin.todoist.api.dispatch.core.DispatchAuthorizedRequestFactory
import ru.pavkin.todoist.api.dispatch.impl.circe.json.DispatchJsonRequestExecutor
import ru.pavkin.todoist.api.dispatch.impl.circe.model.DispatchModelAPI

object dto extends DTODecoders with ParserAPISuite {

  type Projects = Vector[Project]
  type Labels = Vector[Label]

  val todoist = new UnauthorizedAPI[DispatchModelAPI.Result, CirceDecoder.Result, Json] {
    private lazy val executor: RequestExecutor.Aux[Req, DispatchJsonRequestExecutor.Result, Json] = new DispatchJsonRequestExecutor

    def authorize(token: Token): API[DispatchModelAPI.Result, CirceDecoder.Result, Json] =
      new DispatchModelAPI(
        new DispatchAuthorizedRequestFactory(token),
        executor
      )
  }
}
