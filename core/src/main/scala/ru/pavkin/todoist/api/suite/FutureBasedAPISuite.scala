package ru.pavkin.todoist.api.suite

import cats.std.FutureInstances
import ru.pavkin.todoist.api.core.UnauthorizedAPI

import scala.concurrent.ExecutionContext

trait FutureBasedAPISuite[F[_], P[_], Base]
  extends FutureInstances {

  /**
    * Returns Unauthorized API client instance
    *
    * @param ec Execution context that will be used internally for all API calls
    *
    */
  def todoist(implicit ec: ExecutionContext): UnauthorizedAPI[F, P, Base]

}
