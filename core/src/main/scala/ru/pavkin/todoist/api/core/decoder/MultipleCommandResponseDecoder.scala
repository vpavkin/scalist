package ru.pavkin.todoist.api.core.decoder

import cats.{Apply, Functor}
import shapeless.{HNil, ::, HList}
import cats.syntax.apply._
import cats.syntax.functor._

trait MultipleCommandResponseDecoder[F[_], Command <: HList, Base]
  extends CommandResponseDecoder[F, Command, Base] {self =>
  type Out <: HList

  def combine[Command2, Out2](other: CommandResponseDecoder.Aux[F, Command2, Base, Out2])
                             (implicit A: Apply[F])
  : MultipleCommandResponseDecoder.Aux[F, Command2 :: Command, Base, Out2 :: self.Out] =
    new MultipleCommandResponseDecoder[F, Command2 :: Command, Base] {
      type Out = Out2 :: self.Out
      def parse(command: Command2 :: Command)(resource: Base): F[Out] =
        self.parse(command.tail)(resource)
          .map2(other.parse(command.head)(resource))((a, b) => b :: a)
    }
}

object MultipleCommandResponseDecoder {
  type Aux[F[_], Command <: HList, Base, Out0 <: HList] =
  MultipleCommandResponseDecoder[F, Command, Base] {type Out = Out0}

  def using[F[_], Command <: HList, Base, Out0 <: HList](f: (Command, Base) => F[Out0]): Aux[F, Command, Base, Out0] =
    new MultipleCommandResponseDecoder[F, Command, Base] {
      type Out = Out0
      def parse(command: Command)(resource: Base): F[Out] = f(command, resource)
    }

  implicit def singleHListDecoder[F[_] : Functor, Command, Base, T]
  (implicit p: SingleCommandResponseDecoder.Aux[F, Command, Base, T])
  : MultipleCommandResponseDecoder.Aux[F, Command :: HNil, Base, T :: HNil] =
    new MultipleCommandResponseDecoder[F, Command :: HNil, Base] {
      type Out = T :: HNil
      def parse(command: Command :: HNil)(resource: Base): F[T :: HNil] =
        p.parse(command.head)(resource).map(_ :: HNil)
    }

  implicit def recurse[F[_] : Apply, CommandH, CommandT <: HList, Base, H, T <: HList]
  (implicit
   h: SingleCommandResponseDecoder.Aux[F, CommandH, Base, H],
   t: MultipleCommandResponseDecoder.Aux[F, CommandT, Base, T])
  : MultipleCommandResponseDecoder.Aux[F, CommandH :: CommandT, Base, H :: T] =
    new MultipleCommandResponseDecoder[F, CommandH :: CommandT, Base] {
      type Out = H :: T
      def parse(command: CommandH :: CommandT)(resource: Base): F[H :: T] =
        t.combine(h).parse(command)(resource)
    }
}
