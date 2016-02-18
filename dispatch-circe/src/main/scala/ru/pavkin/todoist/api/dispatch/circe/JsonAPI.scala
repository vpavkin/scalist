package ru.pavkin.todoist.api.dispatch.circe

import dispatch.Req
import io.circe.{Decoder, Json}
import ru.pavkin.todoist.api.Token
import ru.pavkin.todoist.api.circe.CirceDecoder.Result
import ru.pavkin.todoist.api.circe.{CirceAPISuite, CirceDecoder}
import ru.pavkin.todoist.api.core._
import ru.pavkin.todoist.api.core.parser.SingleResourceParser
import ru.pavkin.todoist.api.dispatch.core.DispatchAuthorizedRequestFactory
import ru.pavkin.todoist.api.dispatch.impl.circe.{DispatchAPI, DispatchJsonRequestExecutor}
import ru.pavkin.todoist.api.suite.{PlainAPISuite, FutureBasedAPISuite}
import shapeless.tag
import shapeless.tag.@@

import scala.concurrent.ExecutionContext

trait JsonAPI
  extends PlainAPISuite[DispatchAPI.Result, CirceDecoder.Result, Json]
    with CirceAPISuite[DispatchAPI.Result]
    with FutureBasedAPISuite[DispatchAPI.Result, CirceDecoder.Result, Json] {

  implicit def labelledParser[T]: Decoder[Json @@ T] = Decoder[Json].map(a => tag[T](a))

  override implicit val projectsParser: SingleResourceParser.Aux[Result, Json, Projects] = projectsDecoder
  override implicit val labelsParser: SingleResourceParser.Aux[Result, Json, Labels] = labelsDecoder

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
