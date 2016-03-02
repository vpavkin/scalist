package ru.pavkin.todoist.api.core.query

import cats.FlatMap
import ru.pavkin.todoist.api.core.decoder.SingleResponseDecoder
import ru.pavkin.todoist.api.core.{HasRawRequest, RequestDefinition}
import ru.pavkin.todoist.api.utils.NotContainsConstraint
import shapeless.{::, HList}

trait MultipleQueryDefinition[F[_], P[_], R <: HList, Base] extends RequestDefinition[F, P, R, Base] {

  def and[RR](implicit
              FM: FlatMap[P],
              NC: R NotContainsConstraint RR,
              ir: HasRawRequest[RR],
              parser: SingleResponseDecoder[P, Base, RR]): MultipleQueryDefinition[F, P, RR :: R, Base]
}
