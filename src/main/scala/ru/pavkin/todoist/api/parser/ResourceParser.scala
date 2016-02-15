package ru.pavkin.todoist.api.parser

import cats.{FlatMap, Apply}
import cats.syntax.flatMap._
import cats.syntax.apply._
import shapeless.{HNil, HList, ::}

//todo: try with phantom State
trait ResourceParser[F[_], Base] {self =>
  type Out
  def parse(resource: Base): F[Out]
}

trait SingleResourceParser[F[_], Base] extends ResourceParser[F, Base] {self =>
  def combine[Out2](other: ResourceParser.Aux[F, Base, Out2])(implicit A: Apply[F]): MultipleResourcesParser.Aux[F, Base, Out2 :: self.Out :: HNil] = new MultipleResourcesParser[F, Base] {
    type Out = Out2 :: self.Out :: HNil
    def parse(resource: Base): F[Out] = self.parse(resource).map2(other.parse(resource))((a, b) => b :: a :: HNil)
  }

  def compose[Out2](other: ResourceParser.Aux[F, Out, Out2])(implicit F: FlatMap[F]): SingleResourceParser.Aux[F, Base, Out2] = new SingleResourceParser[F, Base] {
    type Out = Out2
    def parse(resource: Base): F[Out] = self.parse(resource).flatMap(other.parse)
  }
}

trait MultipleResourcesParser[F[_], Base] extends ResourceParser[F, Base] {self =>
  type Out <: HList

  def combine[Out2](other: ResourceParser.Aux[F, Base, Out2])(implicit A: Apply[F]): MultipleResourcesParser.Aux[F, Base, Out2 :: self.Out] = new MultipleResourcesParser[F, Base] {
    type Out = Out2 :: self.Out
    def parse(resource: Base): F[Out] = self.parse(resource).map2(other.parse(resource))((a, b) => b :: a)
  }
}

object ResourceParser {
  type Aux[F[_], Base, Out0] = ResourceParser[F, Base] {type Out = Out0}
  def apply[F[_], Base, Out](implicit ev: Aux[F, Base, Out]): ResourceParser[F, Base] = ev

  def apply[F[_], Base, Out0](f: Base => F[Out0]) = new SingleResourceParser[F, Base] {
    type Out = Out0
    def parse(resource: Base): F[Out0] = f(resource)
  }
}

object SingleResourceParser {
  type Aux[F[_], Base, Out0] = SingleResourceParser[F, Base] {type Out = Out0}
  def using[F[_], Base, Out0](f: Base => F[Out0]): SingleResourceParser.Aux[F, Base, Out0] = new SingleResourceParser[F, Base] {
    type Out = Out0
    def parse(resource: Base): F[Out] = f(resource)
  }
}

object MultipleResourcesParser {
  type Aux[F[_], Base, Out0 <: HList] = MultipleResourcesParser[F, Base] {type Out = Out0}
}

