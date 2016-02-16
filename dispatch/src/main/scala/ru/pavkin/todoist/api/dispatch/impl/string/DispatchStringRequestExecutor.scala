package ru.pavkin.todoist.api.dispatch.impl.string

import cats.{Apply, Functor}
import cats.data.Xor
import cats.syntax.xor._
import cats.std.future._
import com.ning.http.client.Response
import dispatch.Defaults._
import dispatch._
import ru.pavkin.todoist.api.dispatch.core.DispatchRequestExecutor
import ru.pavkin.todoist.api.dispatch.impl.string.DispatchStringRequestExecutor._

import scala.concurrent.Future

object DispatchStringRequestExecutor {

  case class HTTPError(code: Int, body: Option[String])

  type Result[T] = Future[Xor[HTTPError, T]]

  type X[T] = Xor[HTTPError, T]
  implicit val functor: Functor[Result] = Functor[Future].compose(Functor[X])
  implicit val apply: Apply[Result] = Apply[Future].compose(Apply[X])
}

class DispatchStringRequestExecutor extends DispatchRequestExecutor[DispatchStringRequestExecutor.Result, String] {

  private def handler(r: Response): Xor[HTTPError, String] =
    if (r.getStatusCode / 100 == 2)
      r.getResponseBody.right
    else
      HTTPError(r.getStatusCode, Option(r.getResponseBody).filter(_.nonEmpty)).left


  def execute(r: Req): Result[String] = Http(r > handler _)

}
