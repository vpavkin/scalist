package ru.pavkin.todoist.api.suite

import cats.std.FutureInstances
import ru.pavkin.todoist.api.core.UnauthorizedAPI

import scala.concurrent.ExecutionContext

trait FutureBasedAPISuite[F[_], P[_], Base]
  extends APISuite[F, P, Base] with FutureInstances {
  def todoist(implicit ec: ExecutionContext): UnauthorizedAPI[F, P, Base]
}
