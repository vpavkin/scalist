package ru.pavkin.todoist.api.core

import ru.pavkin.todoist.api.RawRequest
import shapeless.{::, HList, HNil}

sealed trait ToRawRequest[R] {
  def rawRequest(o: R): RawRequest
}

object ToRawRequest {

  def apply[A](implicit ev: ToRawRequest[A]): ToRawRequest[A] = ev

  def apply[A](f: A => RawRequest): ToRawRequest[A] = new ToRawRequest[A] {
    def rawRequest(o: A): RawRequest = f(o)
  }

  implicit def single[T](implicit T: ToRawRequest[T]): ToRawRequest[T :: HNil] =
    ToRawRequest[T :: HNil]((l: T :: HNil) => T.rawRequest(l.head))

  implicit def recurse[H, T <: HList](implicit H: ToRawRequest[H], T: ToRawRequest[T]): ToRawRequest[H :: T] =
    ToRawRequest[H :: T]((l: H :: T) => H.rawRequest(l.head) ++ T.rawRequest(l.tail))
}


