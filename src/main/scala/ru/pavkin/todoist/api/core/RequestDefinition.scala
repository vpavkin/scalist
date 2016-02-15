package ru.pavkin.todoist.api.core

import cats.FlatMap
import ru.pavkin.todoist.api.parser.SingleResourceParser
import ru.pavkin.todoist.api.utils.NotContains
import shapeless._

trait RequestDefinition[F[_], P[_], R, Base] {
  implicit def itr: IsResource[R]

  type Out

  def execute: F[Out]
}

trait SingleReadResourceDefinition[F[_], P[_], R, Base] extends RequestDefinition[F, P, R, Base] {

  def and[RR](implicit
              FM: FlatMap[P],
              NEQ: RR <:!< R,
              ir: IsResource[RR],
              parser: SingleResourceParser.Aux[P, Base, RR]): MultipleReadResourceDefinition[F, P, RR :: R :: HNil, Base]
}

trait MultipleReadResourceDefinition[F[_], P[_], R <: HList, Base] extends RequestDefinition[F, P, R, Base] {

  def and[RR](implicit
              FM: FlatMap[P],
              NC: R NotContains RR,
              ir: IsResource[RR],
              parser: SingleResourceParser.Aux[P, Base, RR]): MultipleReadResourceDefinition[F, P, RR :: R, Base]
}
