package ru.pavkin.todoist.api.core

import ru.pavkin.todoist.api.RawRequest
import shapeless.{::, HList, HNil}

sealed trait HasRawRequest[R] {
  def rawRequest: RawRequest
}

object HasRawRequest {

  def apply[A](implicit ev: HasRawRequest[A]): HasRawRequest[A] = ev

  def apply[A](f: => RawRequest): HasRawRequest[A] = new HasRawRequest[A] {
    def rawRequest: RawRequest = f
  }

  implicit def single[T](implicit T: HasRawRequest[T]): HasRawRequest[T :: HNil] =
    HasRawRequest[T :: HNil](T.rawRequest)

  implicit def recurse[H, T <: HList](implicit H: HasRawRequest[H], T: HasRawRequest[T]): HasRawRequest[H :: T] =
    HasRawRequest[H :: T](H.rawRequest ++ T.rawRequest)
}
