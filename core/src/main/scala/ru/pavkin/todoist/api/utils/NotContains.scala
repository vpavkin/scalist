package ru.pavkin.todoist.api.utils

import scala.annotation.implicitNotFound
import shapeless._

@implicitNotFound("Implicit not found: NotContains[${L}, ${U}]. HList already contains type ${U}")
trait NotContains[L <: HList, U]

object NotContains {
  implicit def nilNotContains[U]: NotContains[HNil, U] =
    new NotContains[HNil, U] {}

  implicit def recurse[H <: HList, T, U](implicit
                                         ev: H NotContains U,
                                         ev2: U =:!= T): NotContains[T :: H, U] =
    new NotContains[T :: H, U] {}
}
