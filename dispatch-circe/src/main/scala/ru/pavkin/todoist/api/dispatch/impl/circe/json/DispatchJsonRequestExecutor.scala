package ru.pavkin.todoist.api.dispatch.impl.circe.json

import cats.Apply
import cats.data.Xor
import cats.std.FutureInstances
import cats.syntax.xor._
import com.ning.http.client.Response
import dispatch.{Http, Req}
import io.circe.parser._
import io.circe.{Json, ParsingFailure}
import ru.pavkin.todoist.api.dispatch.core.DispatchRequestExecutor
import ru.pavkin.todoist.api.utils.ComposeApply

import scala.concurrent.{ExecutionContext, Future}

object DispatchJsonRequestExecutor extends FutureInstances with ComposeApply {
  sealed trait Error
  case class HTTPError(code: Int, body: Option[String]) extends Error
  case class ParsingError(underlying: ParsingFailure) extends Error

  type X[T] = Xor[Error, T]
  type Result[T] = Future[X[T]]

  implicit def applyInstance(implicit ec: ExecutionContext): Apply[Result] = composeApply
}

class DispatchJsonRequestExecutor(implicit ec: ExecutionContext)
  extends DispatchRequestExecutor[DispatchJsonRequestExecutor.Result, Json] {

  import DispatchJsonRequestExecutor._

  private def handler(r: Response): Xor[Error, Json] =
    if (r.getStatusCode / 100 == 2)
      parse(r.getResponseBody).leftMap(ParsingError)
    else
      HTTPError(r.getStatusCode, Option(r.getResponseBody).filter(_.nonEmpty)).left

  def execute(r: Req): Result[Json] = Http(r > handler _)

}
