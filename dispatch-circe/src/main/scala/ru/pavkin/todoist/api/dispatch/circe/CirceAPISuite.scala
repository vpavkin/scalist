package ru.pavkin.todoist.api.dispatch.circe

import cats.data.Xor
import dispatch.Req
import io.circe.{DecodingFailure, Json}
import ru.pavkin.todoist.api.Token
import ru.pavkin.todoist.api.circe.CirceDecoder
import ru.pavkin.todoist.api.circe.CirceDecoder.Result
import ru.pavkin.todoist.api.circe.decoders.CirceDTODecoders
import ru.pavkin.todoist.api.circe.dto.CirceDTOCommands
import ru.pavkin.todoist.api.circe.encoders.CirceDTOEncoders
import ru.pavkin.todoist.api.core._
import ru.pavkin.todoist.api.core.decoder.SingleResponseDecoder
import ru.pavkin.todoist.api.core.dto.{AllResources, RawCommandResult}
import ru.pavkin.todoist.api.dispatch.core.DispatchAuthorizedRequestFactory
import ru.pavkin.todoist.api.dispatch.impl.circe.{DispatchAPI, DispatchJsonRequestExecutor}
import ru.pavkin.todoist.api.suite.{AbstractDTOCommandAPISuite, AbstractDTOQueryAPISuite, FutureBasedAPISuite}
import scala.concurrent.ExecutionContext

trait CirceAPISuite
  extends CirceDTODecoders
    with CirceDTOEncoders
    with CirceDTOCommands
    with FutureBasedAPISuite[DispatchAPI.Result, CirceDecoder.Result, Json]
    with AbstractDTOQueryAPISuite[DispatchAPI.Result, CirceDecoder.Result, Json, AllResources]
    with AbstractDTOCommandAPISuite[DispatchAPI.Result, CirceDecoder.Result, Json, RawCommandResult] {

  implicit def commandDtoDecoder: SingleResponseDecoder[Result, Json, RawCommandResult] =
    new CirceDecoder[RawCommandResult]

  implicit def resourceDtoDecoder: SingleResponseDecoder[CirceDecoder.Result, Json, AllResources] =
    new CirceDecoder[AllResources]

  def dtoDecodingError[T](msg: String): CirceDecoder.Result[T] = Xor.Left(DecodingFailure(msg, Nil))

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
