package ru.pavkin.todoist.api.core.decoder

import cats.{FlatMap, Apply}
import shapeless.{HNil, ::}
import cats.syntax.flatMap._
import cats.syntax.apply._

trait SingleCommandResponseDecoder[F[_], Command, Base] extends CommandResponseDecoder[F, Command, Base] {self =>
  def combine[Command2, Out2](other: CommandResponseDecoder.Aux[F, Command2, Base, Out2])
                             (implicit A: Apply[F])
  : MultipleCommandResponseDecoder.Aux[F, Command2 :: Command :: HNil, Base, Out2 :: self.Out :: HNil] =
    new MultipleCommandResponseDecoder[F, Command2 :: Command :: HNil, Base] {
      type Out = Out2 :: self.Out :: HNil

      def parse(command: Command2 :: Command :: HNil)(resource: Base): F[Out] =
        self.parse(command.tail.head)(resource)
          .map2(other.parse(command.head)(resource))((a, b) => b :: a :: HNil)
    }

  def compose[Out2](other: CommandResponseDecoder.Aux[F, Command, Out, Out2])
                   (implicit F: FlatMap[F]): SingleCommandResponseDecoder.Aux[F, Command, Base, Out2] =
    new SingleCommandResponseDecoder[F, Command, Base] {
      type Out = Out2
      def parse(command: Command)(resource: Base): F[Out] =
        self.parse(command: Command)(resource).flatMap(other.parse(command: Command))
    }
}


object SingleCommandResponseDecoder {
  type Aux[F[_], Command, Base, Out0] = SingleCommandResponseDecoder[F, Command, Base] {type Out = Out0}
  def using[F[_], Command, Base, Out0](f: (Command, Base) => F[Out0]): Aux[F, Command, Base, Out0] =
    new SingleCommandResponseDecoder[F, Command, Base] {
      type Out = Out0
      def parse(command: Command)(resource: Base): F[Out] = f(command, resource)
    }
}
