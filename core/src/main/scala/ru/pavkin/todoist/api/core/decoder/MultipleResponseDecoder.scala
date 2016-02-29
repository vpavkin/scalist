package ru.pavkin.todoist.api.core.decoder

import cats.{Apply, Functor}
import shapeless.{HNil, ::, HList}
import cats.syntax.apply._
import cats.syntax.functor._

trait MultipleResponseDecoder[F[_], Base, Out <: HList] extends ResponseDecoder[F, Base, Out] {self =>

  def combine[Out2](other: ResponseDecoder[F, Base, Out2])
                   (implicit A: Apply[F]): MultipleResponseDecoder[F, Base, Out2 :: Out] =
    new MultipleResponseDecoder[F, Base, Out2 :: Out] {
      def parse(resource: Base): F[Out2 :: Out] = self.parse(resource).map2(other.parse(resource))((a, b) => b :: a)
    }
}

object MultipleResponseDecoder {

  def using[F[_], Base, Out0 <: HList](f: Base => F[Out0]): MultipleResponseDecoder[F, Base, Out0] =
    new MultipleResponseDecoder[F, Base, Out0] {
      def parse(resource: Base): F[Out0] = f(resource)
    }

  implicit def singleHListParser[F[_] : Functor, Base, T](implicit p: SingleResponseDecoder[F, Base, T])
  : MultipleResponseDecoder[F, Base, T :: HNil] =
    new MultipleResponseDecoder[F, Base, T :: HNil] {
      def parse(resource: Base): F[T :: HNil] = p.parse(resource).map(_ :: HNil)
    }

  implicit def recurse[F[_] : Apply, Base, H, T <: HList](implicit
                                                          h: SingleResponseDecoder[F, Base, H],
                                                          t: MultipleResponseDecoder[F, Base, T])
  : MultipleResponseDecoder[F, Base, H :: T] =
    new MultipleResponseDecoder[F, Base, H :: T] {
      def parse(resource: Base): F[H :: T] = t.combine(h).parse(resource)
    }
}
