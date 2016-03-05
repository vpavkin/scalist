package ru.pavkin.todoist.api.core

import cats.Functor
import ru.pavkin.todoist.api._
import ru.pavkin.todoist.api.utils.{Flattener, Produce}

abstract class CompositeExecutedRequestDefinition[F[_], L[_], P[_], R, Req, Base]
    (requestFactory: RawRequest Produce Req,
     executor: RequestExecutor.Aux[Req, L, Base],
     flattener: Flattener[F, L, P])
    (override implicit val F: Functor[L])
  extends ExecutedRequestDefinition[F, L, P, R, Req, Base] {

  def toRawRequest: RawRequest
  def load: L[Base] = executor.execute(requestFactory.produce(toRawRequest))
  def flatten(r: L[P[R]]): F[R] = flattener.flatten(r)
}
