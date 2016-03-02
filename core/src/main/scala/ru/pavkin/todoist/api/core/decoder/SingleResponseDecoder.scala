package ru.pavkin.todoist.api.core.decoder

import cats.{FlatMap, Apply}
import shapeless.{HList, HNil, ::}
import cats.syntax.flatMap._
import cats.syntax.apply._

trait SingleResponseDecoder[F[_], Base, Out] extends ResponseDecoder[F, Base, Out] {self =>
  def combine[Out2](other: ResponseDecoder[F, Base, Out2])
                   (implicit A: Apply[F]): MultipleResponseDecoder[F, Base, Out2 :: Out :: HNil] =
    new MultipleResponseDecoder[F, Base, Out2 :: Out :: HNil] {
      def parse(resource: Base): F[Out2 :: Out :: HNil] =
        self.parse(resource).map2(other.parse(resource))((a, b) => b :: a :: HNil)
    }

  def compose[Out2](other: SingleResponseDecoder[F, Out, Out2])
                   (implicit F: FlatMap[F]): SingleResponseDecoder[F, Base, Out2] =
    new SingleResponseDecoder[F, Base, Out2] {
      def parse(resource: Base): F[Out2] = self.parse(resource).flatMap(other.parse)
    }

  def compose[Out2 <: HList](other: MultipleResponseDecoder[F, Out, Out2])
                            (implicit F: FlatMap[F]): MultipleResponseDecoder[F, Base, Out2] =
    new MultipleResponseDecoder[F, Base, Out2] {
      def parse(resource: Base): F[Out2] = self.parse(resource).flatMap(other.parse)
    }

  def compose[Out2, Command]
  (other: SingleCommandResponseDecoder.Aux[F, Command, Out, Out2])
  (implicit F: FlatMap[F]): SingleCommandResponseDecoder.Aux[F, Command, Base, Out2] =
    new SingleCommandResponseDecoder[F, Command, Base] {
      type Out = Out2
      def parse(command: Command)(resource: Base): F[Out] =
        self.parse(resource).flatMap(other.parse(command))
    }

  def compose[Out2 <: HList, Command <: HList]
  (other: MultipleCommandResponseDecoder.Aux[F, Command, Out, Out2])
  (implicit F: FlatMap[F]): MultipleCommandResponseDecoder.Aux[F, Command, Base, Out2] =
    new MultipleCommandResponseDecoder[F, Command, Base] {
      type Out = Out2
      def parse(command: Command)(resource: Base): F[Out] =
        self.parse(resource).flatMap(other.parse(command))
    }
}


object SingleResponseDecoder {
  def apply[F[_], Base, Out0](implicit
                              ev: SingleResponseDecoder[F, Base, Out0]): SingleResponseDecoder[F, Base, Out0] = ev

  def using[F[_], Base, Out0](f: Base => F[Out0]): SingleResponseDecoder[F, Base, Out0] =
    new SingleResponseDecoder[F, Base, Out0] {
      def parse(resource: Base): F[Out0] = f(resource)
    }
}
