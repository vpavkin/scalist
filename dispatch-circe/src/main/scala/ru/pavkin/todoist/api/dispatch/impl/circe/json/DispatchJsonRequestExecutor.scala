package ru.pavkin.todoist.api.dispatch.impl.circe.json

import cats.Apply
import cats.data.Xor
import cats.std.FutureInstances
import dispatch.Req
import io.circe.parser._
import io.circe.{Json, ParsingFailure}
import ru.pavkin.todoist.api.dispatch.core.{DispatchRequestExecutor, DispatchStringRequestExecutor}
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

  val underlying = new DispatchStringRequestExecutor

  def execute(r: Req): Result[Json] =
    underlying.execute(r).map {
      _.leftMap(e => HTTPError(e.code, e.body): Error)
        .flatMap(parse(_).leftMap(ParsingError))
    }

}
