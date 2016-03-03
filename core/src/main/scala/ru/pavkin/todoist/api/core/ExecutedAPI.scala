package ru.pavkin.todoist.api.core

import cats.Functor
import ru.pavkin.todoist.api.RawRequest
import ru.pavkin.todoist.api.utils.{Flattener, Produce}

trait ExecutedAPI[F[_], L[_], P[_], Req, Base] {

  implicit def F: Functor[L]

  def requestFactory: Produce[RawRequest, Req]
  def executor: RequestExecutor.Aux[Req, L, Base]
  def flattener: Flattener[F, L, P]
}
