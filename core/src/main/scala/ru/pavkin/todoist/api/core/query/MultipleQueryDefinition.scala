package ru.pavkin.todoist.api.core.query

import cats.FlatMap
import ru.pavkin.todoist.api.core.decoder.SingleResponseDecoder
import ru.pavkin.todoist.api.core.{HasRawRequest, RequestDefinition}
import ru.pavkin.todoist.api.utils.NotContainsConstraint
import shapeless.{::, HList}

/**
  * A definition of a multiple resources query.
  * The execution result is an HList of requested resources
  *
  * Call `execute` to perform the request and get the result (under the effect)
  */
trait MultipleQueryDefinition[F[_], P[_], R <: HList, Base] extends RequestDefinition[F, P, R, Base] {

  /**
    * Returns a new request definition, that after execution will return
    * all originally requested resources plus the added one (in a form of an `HList`)
    *
    * See [[ru.pavkin.todoist.api.core.AuthorizedAPI.getAll]] for details on working with
    * multiple resources response
    */
  def and[RR](implicit
              FM: FlatMap[P],
              NC: R NotContainsConstraint RR,
              ir: HasRawRequest[RR],
              parser: SingleResponseDecoder[P, Base, RR]): MultipleQueryDefinition[F, P, RR :: R, Base]
}
