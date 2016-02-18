package ru.pavkin.todoist.api.dispatch.impl.string

import cats.Apply
import cats.data.Xor
import cats.std.FutureInstances
import cats.syntax.xor._
import com.ning.http.client.Response
import dispatch._
import ru.pavkin.todoist.api.dispatch.core.DispatchRequestExecutor
import ru.pavkin.todoist.api.utils.ComposeApply

import scala.concurrent.{ExecutionContext, Future}

object DispatchStringRequestExecutor extends FutureInstances with ComposeApply {

  case class HTTPError(code: Int, body: Option[String])

  type X[T] = Xor[HTTPError, T]
  type Result[T] = Future[X[T]]

  implicit def applyInstance(implicit ec: ExecutionContext): Apply[Result] = composeApply
}

class DispatchStringRequestExecutor(implicit ec: ExecutionContext)
  extends DispatchRequestExecutor[DispatchStringRequestExecutor.Result, String] {

  import DispatchStringRequestExecutor._

  private def handler(r: Response): Xor[HTTPError, String] =
    if (r.getStatusCode / 100 == 2)
      r.getResponseBody.right
    else
      HTTPError(r.getStatusCode, Option(r.getResponseBody).filter(_.nonEmpty)).left

  def execute(r: Req): Result[String] = Http(r > handler _)

}
