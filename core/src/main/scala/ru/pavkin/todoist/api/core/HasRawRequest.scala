package ru.pavkin.todoist.api.core

import ru.pavkin.todoist.api._
import shapeless.{::, HList, HNil}
import cats.syntax.semigroup._

sealed trait HasRawRequest[R] {
  def rawRequest: RawRequest
}

object HasRawRequest {

  val RESOURCE_TYPES = "resource_types"

  def apply[A](implicit ev: HasRawRequest[A]): HasRawRequest[A] = ev

  def apply[A](f: => RawRequest): HasRawRequest[A] = new HasRawRequest[A] {
    def rawRequest: RawRequest = f
  }

  def resource[A](f: => List[String]): HasRawRequest[A] = new HasRawRequest[A] {
    def rawRequest: RawRequest = Map(RESOURCE_TYPES -> f.map("\"" + _ + "\""))
  }

  implicit def single[T](implicit T: HasRawRequest[T]): HasRawRequest[T :: HNil] =
    HasRawRequest[T :: HNil](T.rawRequest)

  implicit def recurse[H, T <: HList](implicit H: HasRawRequest[H], T: HasRawRequest[T]): HasRawRequest[H :: T] =
    HasRawRequest[H :: T](H.rawRequest.combine(T.rawRequest))
}
