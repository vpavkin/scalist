package ru.pavkin.todoist.api.core

import cats.std.FutureInstances

import scala.concurrent.ExecutionContext

trait FutureBasedAPISuite[F[_], P[_], Base]
  extends APISuite[F, P, Base] with FutureInstances {
  def todoist(implicit ec: ExecutionContext): UnauthorizedAPI[F, P, Base]
}
