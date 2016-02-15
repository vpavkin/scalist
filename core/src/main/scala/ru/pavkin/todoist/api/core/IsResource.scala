package ru.pavkin.todoist.api.core

import shapeless.{::, HList, HNil}

sealed trait IsResource[R] {
  def strings: Vector[String]
}

object IsResource {

  def apply[A](implicit ev: IsResource[A]): IsResource[A] = ev
  def apply[A](f: => Vector[String]): IsResource[A] = new IsResource[A] {
    def strings: Vector[String] = f
  }

  implicit def single[T](implicit T: IsResource[T]): IsResource[T :: HNil] =
    IsResource[T :: HNil](T.strings)

  implicit def recurse[H, T <: HList](implicit H: IsResource[H], T: IsResource[T]): IsResource[H :: T] =
    IsResource[H :: T](H.strings ++ T.strings)
}
