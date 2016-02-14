package ru.pavkin.todoist.api.core

import shapeless.{HList, HNil, ::}

sealed trait IsResource[ResourceType] {
  type Repr

  def strings: Vector[String]
}

object IsResource {

  type Aux[A, Repr0] = IsResource[A] {type Repr = Repr0}

  def apply[A](implicit ev: IsResource[A]): IsResource.Aux[A, ev.Repr] = ev
  def apply[A, R](f: => Vector[String]): IsResource.Aux[A, R] = new IsResource[A] {
    type Repr = R
    def strings: Vector[String] = f
  }

  implicit def single[T](implicit T: IsResource[T]): IsResource.Aux[T :: HNil, T.Repr :: HNil] =
    IsResource[T :: HNil, T.Repr :: HNil](T.strings)

  implicit def recurse[H, T <: HList, RT <: HList](implicit H: IsResource[H],
                                                   T: IsResource.Aux[T, RT]): IsResource.Aux[H :: T, H.Repr :: RT] =
    IsResource[H :: T, H.Repr :: RT](H.strings ++ T.strings)
}
