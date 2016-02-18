package ru.pavkin.todoist.api.dispatch.impl.circe.model

import cats.data.Xor
import cats.std.FutureInstances
import cats.{Apply, Functor}
import dispatch.Req
import io.circe.{DecodingFailure, Json}
import ru.pavkin.todoist.api.core.parser.ParserBasedAPI
import ru.pavkin.todoist.api.core.{AuthorizedRequestFactory, RequestExecutor}
import ru.pavkin.todoist.api.dispatch.impl.circe.json.DispatchJsonRequestExecutor
import ru.pavkin.todoist.api.dispatch.impl.circe.json.DispatchJsonRequestExecutor.ParsingError
import ru.pavkin.todoist.api.utils.{ComposeApply, Flattener}

import scala.concurrent.{ExecutionContext, Future}

object DispatchModelAPI extends FutureInstances with ComposeApply {

  sealed trait Error
  case class DecodingError(underlying: io.circe.Error) extends Error
  case class HTTPError(code: Int, body: Option[String]) extends Error

  type L[T] = DispatchJsonRequestExecutor.Result[T]
  type P[T] = Xor[DecodingFailure, T]

  type X[T] = Xor[Error, T]
  type Result[T] = Future[X[T]]
  implicit def applyInstance(implicit ec: ExecutionContext): Apply[Result] = composeApply

  class ModelFlattener(implicit ec: ExecutionContext) extends Flattener[Result, L, P] {
    override def flatten[T](o: Future[Xor[DispatchJsonRequestExecutor.Error, Xor[DecodingFailure, T]]])
    : Future[Xor[Error, T]] =
      o.map {
        _.map(_.leftMap(DecodingError))
          .leftMap {
            case DispatchJsonRequestExecutor.HTTPError(code, body) =>
              HTTPError(code, body)
            case ParsingError(underlying) =>
              DecodingError(underlying)
          }.flatMap(identity)
      }
  }
}

import DispatchModelAPI._

class DispatchModelAPI(override val requestFactory: AuthorizedRequestFactory[Vector[String], Req],
                       override val executor: RequestExecutor.Aux[Req, DispatchJsonRequestExecutor.Result, Json])
                      (override implicit val F: Functor[L],
                       implicit val ec: ExecutionContext)
  extends ParserBasedAPI[Result, L, P, Req, Json] {

  override val flattener = new ModelFlattener

}
