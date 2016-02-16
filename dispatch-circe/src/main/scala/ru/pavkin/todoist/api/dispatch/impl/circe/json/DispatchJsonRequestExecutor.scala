package ru.pavkin.todoist.api.dispatch.impl.circe.json

import cats.{Apply, Functor}
import cats.data.Xor
import cats.syntax.xor._
import cats.std.future._
import com.ning.http.client.Response
import dispatch.{Http, Req}
import dispatch.Defaults._
import io.circe.{ParsingFailure, Json}
import io.circe.parser._
import ru.pavkin.todoist.api.dispatch.core.DispatchRequestExecutor
import ru.pavkin.todoist.api.dispatch.impl.circe.json.DispatchJsonRequestExecutor._

import scala.concurrent.Future

object DispatchJsonRequestExecutor {
  sealed trait Error
  case class HTTPError(code: Int, body: Option[String]) extends Error
  case class ParsingError(underlying: ParsingFailure) extends Error

  type Result[T] = Future[Xor[Error, T]]

  type X[T] = Xor[Error, T]
  implicit val functor: Functor[Result] = Functor[Future].compose(Functor[X])
  implicit val apply: Apply[Result] = Apply[Future].compose(Apply[X])
}

class DispatchJsonRequestExecutor extends DispatchRequestExecutor[DispatchJsonRequestExecutor.Result, Json] {

  private def handler(r: Response): Xor[Error, Json] =
    if (r.getStatusCode / 100 == 2)
      parse(r.getResponseBody).leftMap(ParsingError)
    else
      HTTPError(r.getStatusCode, Option(r.getResponseBody).filter(_.nonEmpty)).left


  def execute(r: Req): Result[Json] = Http(r > handler _)

}
