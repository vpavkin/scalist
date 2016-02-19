package ru.pavkin.todoist.api.core.decoder

import cats.{Apply, Functor}
import shapeless.{HNil, ::, HList}
import cats.syntax.apply._
import cats.syntax.functor._

trait MultipleResponseDecoder[F[_], Base] extends ResponseDecoder[F, Base] {self =>
  type Out <: HList

  def combine[Out2](other: ResponseDecoder.Aux[F, Base, Out2])
                   (implicit A: Apply[F]): MultipleResponseDecoder.Aux[F, Base, Out2 :: self.Out] =
    new MultipleResponseDecoder[F, Base] {
      type Out = Out2 :: self.Out
      def parse(resource: Base): F[Out] = self.parse(resource).map2(other.parse(resource))((a, b) => b :: a)
    }
}

object MultipleResponseDecoder {
  type Aux[F[_], Base, Out0 <: HList] = MultipleResponseDecoder[F, Base] {type Out = Out0}

  def using[F[_], Base, Out0 <: HList](f: Base => F[Out0]): Aux[F, Base, Out0] = new MultipleResponseDecoder[F, Base] {
    type Out = Out0
    def parse(resource: Base): F[Out] = f(resource)
  }

  implicit def singleHListParser[F[_] : Functor, Base, T](implicit p: SingleResponseDecoder.Aux[F, Base, T])
  : MultipleResponseDecoder.Aux[F, Base, T :: HNil] =
    new MultipleResponseDecoder[F, Base] {
      type Out = T :: HNil
      def parse(resource: Base): F[T :: HNil] = p.parse(resource).map(_ :: HNil)
    }

  implicit def recurse[F[_] : Apply, Base, H, T <: HList](implicit
                                                          h: SingleResponseDecoder.Aux[F, Base, H],
                                                          t: MultipleResponseDecoder.Aux[F, Base, T])
  : MultipleResponseDecoder.Aux[F, Base, H :: T] =
    new MultipleResponseDecoder[F, Base] {
      type Out = H :: T
      def parse(resource: Base): F[H :: T] = t.combine(h).parse(resource)
    }
}
