package ru.pavkin.todoist.api.utils

import shapeless._

import scala.annotation.implicitNotFound

@implicitNotFound("Implicit not found: NotContains[${L}, ${U}]. HList already contains type ${U}")
trait NotContains[L <: HList, U]

object NotContains {
  implicit def nilNotContains[U] = new NotContains[HNil, U] {}
  implicit def recurse[H <: HList, T, U](implicit ev: H NotContains U, ev2: U =:!= T) = new NotContains[T :: H, U] {}
}
