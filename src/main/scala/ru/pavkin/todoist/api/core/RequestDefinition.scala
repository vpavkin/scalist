package ru.pavkin.todoist.api.core

import ru.pavkin.todoist.api.utils.NotContains
import shapeless._

trait RequestDefinition[F[_]] {
  type Res

  def execute: F[Res]
}

trait SingleReadResourceDefinition[F[_], T <: ReadResourceType, R] extends RequestDefinition[F] {
  implicit def itr: IsResource.Aux[T, R]
  type Res

  def and[TT <: ReadResourceType](implicit NEQ: TT <:!< T,
                                  ITR: IsResource[TT]): MultipleReadResourceDefinition[F, TT :: T :: HNil, ITR.Repr :: R :: HNil]
}

trait MultipleReadResourceDefinition[F[_], T <: HList, R <: HList] extends RequestDefinition[F] {
  implicit def lub: LUBConstraint[T, ReadResourceType]
  implicit def itr: IsResource.Aux[T, R]
  type Res

  def and[TT <: ReadResourceType](implicit NC: T NotContains TT,
                                  ITR: IsResource[TT]): MultipleReadResourceDefinition[F, TT :: T, ITR.Repr :: R]
}
