package ru.pavkin.todoist.api.core.query

import cats.FlatMap
import ru.pavkin.todoist.api.core.decoder.SingleResponseDecoder
import ru.pavkin.todoist.api.core.{HasRawRequest, RequestDefinition}
import shapeless.{::, <:!<, HNil}

/**
  * A definition of a single resource query.
  *
  * Call `execute` to perform the request and get the result (under the effect)
  */
trait SingleQueryDefinition[F[_], P[_], R, Base] extends RequestDefinition[F, P, R, Base] {

  /**
    * Returns a new request definition, that after execution will return
    * both resources (in a form of an `HList`)
    *
    * Equivalent of calling:
    * {{{api.getAll[R2 :: R1 :: HNil]}}}
    *
    * See [[ru.pavkin.todoist.api.core.AuthorizedAPI.getAll]] for details on working with
    * multiple resources response
    */
  def and[RR](implicit
              FM: FlatMap[P],
              NEQ: RR <:!< R,
              ir: HasRawRequest[RR],
              parser: SingleResponseDecoder[P, Base, RR])
  : MultipleQueryDefinition[F, P, RR :: R :: HNil, Base]
}
