package ru.pavkin.todoist.api.core

import cats.Functor
import ru.pavkin.todoist.api.RawRequest
import ru.pavkin.todoist.api.core.parser.{MultipleResourcesParser, SingleResourceParser}
import ru.pavkin.todoist.api.core.query._
import ru.pavkin.todoist.api.utils.{Flattener, Produce}
import shapeless.HList

trait ExecutedAPI[F[_], L[_], P[_], Req, Base] extends API[F, P, Base] {

  implicit def F: Functor[L]

  def requestFactory: Produce[RawRequest, Req]
  def executor: RequestExecutor.Aux[Req, L, Base]
  def flattener: Flattener[F, L, P]

  def get[R](implicit
             IR: HasRawRequest[R],
             parser: SingleResourceParser.Aux[P, Base, R]): SingleQueryDefinition[F, P, R, Base] =
    new SingleQueryRequestDefinition[F, L, P, R, Req, Base](requestFactory, executor, flattener, parser)

  def getAll[R <: HList](implicit
                         IR: HasRawRequest[R],
                         parser: MultipleResourcesParser.Aux[P, Base, R])
  : MultipleQueryDefinition[F, P, R, Base] =
    new MultipleQueryRequestDefinition[F, L, P, R, Req, Base](requestFactory, executor, flattener, parser)
}
