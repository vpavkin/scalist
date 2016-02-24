package ru.pavkin.todoist.api.utils

import scala.annotation.implicitNotFound
import shapeless._

@implicitNotFound("Implicit not found: NotContains[${L}, ${U}]. HList already contains type ${U}")
trait NotContainsConstraint[L <: HList, U]

object NotContainsConstraint {
  def apply[L <: HList, U](implicit ev: NotContainsConstraint[L, U]): NotContainsConstraint[L, U] = ev

  implicit def nilNotContains[U]: NotContainsConstraint[HNil, U] =
    new NotContainsConstraint[HNil, U] {}

  implicit def recurse[H <: HList, T, U](implicit
                                         ev: H NotContainsConstraint U,
                                         ev2: U =:!= T): NotContainsConstraint[T :: H, U] =
    new NotContainsConstraint[T :: H, U] {}
}
