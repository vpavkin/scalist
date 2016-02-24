package ru.pavkin.todoist.api.utils

import scala.annotation.implicitNotFound
import shapeless._

/**
 * Type class witnessing that all elements of `L` have distinct types
 */
@implicitNotFound("Implicit not found: shapeless.IsDistinctConstraint[${L}]. Some elements have the same type.")
trait IsDistinctConstraint[L <: HList] extends Serializable

object IsDistinctConstraint {

  def apply[L <: HList](implicit idc: IsDistinctConstraint[L]): IsDistinctConstraint[L] = idc

  implicit def hnilIsDistinct = new IsDistinctConstraint[HNil] {}
  implicit def hlistIsDistinct[H, T <: HList](implicit d: IsDistinctConstraint[T],
                                                      nc: NotContainsConstraint[T, H]): IsDistinctConstraint[H :: T] =
    new IsDistinctConstraint[H :: T] {}
}
