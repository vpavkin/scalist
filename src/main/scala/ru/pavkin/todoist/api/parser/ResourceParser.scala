package ru.pavkin.todoist.api.parser

import cats.{FlatMap, Apply}
import cats.syntax.flatMap._
import cats.syntax.apply._
import shapeless.{HNil, HList, ::}

//todo: try with phantom State
trait ResourceParser[F[_], R] {self =>
  type To
  def parse(resource: R): F[To]
}

trait SingleResourceParser[F[_], R] extends ResourceParser[F, R] {self =>
  def combine[To2](other: ResourceParser.Aux[F, R, To2])(implicit A: Apply[F]): MultipleResourcesParser.Aux[F, R, To2 :: self.To :: HNil] = new MultipleResourcesParser[F, R] {
    type To = To2 :: self.To :: HNil
    def parse(resource: R): F[To] = self.parse(resource).map2(other.parse(resource))((a, b) => b :: a :: HNil)
  }

  def compose[To2](other: ResourceParser.Aux[F, To, To2])(implicit F: FlatMap[F]): SingleResourceParser.Aux[F, R, To2] = new SingleResourceParser[F, R] {
    type To = To2
    def parse(resource: R): F[To2] = self.parse(resource).flatMap(other.parse)
  }
}

trait MultipleResourcesParser[F[_], R] extends ResourceParser[F, R] {self =>
  type To <: HList

  def combine[To2](other: ResourceParser.Aux[F, R, To2])(implicit A: Apply[F]): MultipleResourcesParser.Aux[F, R, To2 :: self.To] = new MultipleResourcesParser[F, R] {
    type To = To2 :: self.To
    def parse(resource: R): F[To] = self.parse(resource).map2(other.parse(resource))((a, b) => b :: a)
  }
}

object ResourceParser {
  type Aux[F[_], R, To0] = ResourceParser[F, R] {type To = To0}
  def apply[F[_], R, To](implicit ev: Aux[F, R, To]): ResourceParser[F, R] = ev

  def apply[F[_], R, To0](f: R => F[To0]) = new SingleResourceParser[F, R] {
    type To = To0
    def parse(resource: R): F[To0] = f(resource)
  }
}

object SingleResourceParser {
  type Aux[F[_], R, To0] = SingleResourceParser[F, R] {type To = To0}
}

object MultipleResourcesParser {
  type Aux[F[_], R, To0 <: HList] = MultipleResourcesParser[F, R] {type To = To0}
}

