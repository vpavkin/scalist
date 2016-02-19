package ru.pavkin.todoist.api.dispatch.circe

import dispatch.Req
import io.circe.Json
import ru.pavkin.todoist.api.Token
import ru.pavkin.todoist.api.circe.decoders.DTODecoders
import ru.pavkin.todoist.api.circe.{CirceAPISuite, CirceDecoder}
import ru.pavkin.todoist.api.core._
import ru.pavkin.todoist.api.core.dto.{Label, Project}
import ru.pavkin.todoist.api.core.parser.SingleResponseDecoder
import ru.pavkin.todoist.api.dispatch.core.DispatchAuthorizedRequestFactory
import ru.pavkin.todoist.api.dispatch.impl.circe.{DispatchAPI, DispatchJsonRequestExecutor}
import ru.pavkin.todoist.api.suite.FutureBasedAPISuite

import scala.concurrent.ExecutionContext

trait DTOAPI
  extends DTODecoders
    with CirceAPISuite[DispatchAPI.Result]
    with FutureBasedAPISuite[DispatchAPI.Result, CirceDecoder.Result, Json] {

  type Projects = Vector[Project]
  type Labels = Vector[Label]

  override implicit val projectsParser: SingleResponseDecoder.Aux[CirceDecoder.Result, Json, Vector[Project]] =
    projectsDecoder

  override implicit val labelsParser: SingleResponseDecoder.Aux[CirceDecoder.Result, Json, Vector[Label]] =
    labelsDecoder

  def todoist(implicit ec: ExecutionContext): UnauthorizedAPI[DispatchAPI.Result, CirceDecoder.Result, Json] =
    new UnauthorizedAPI[DispatchAPI.Result, CirceDecoder.Result, Json] {
      private lazy val executor: RequestExecutor.Aux[Req, DispatchJsonRequestExecutor.Result, Json] =
        new DispatchJsonRequestExecutor

      def withToken(token: Token): API[DispatchAPI.Result, CirceDecoder.Result, Json] =
        new DispatchAPI(
          new DispatchAuthorizedRequestFactory(token),
          executor
        )
    }
}
