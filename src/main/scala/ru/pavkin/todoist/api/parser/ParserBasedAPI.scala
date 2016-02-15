package ru.pavkin.todoist.api.parser

import cats.Functor
import ru.pavkin.todoist.api.core._
import ru.pavkin.todoist.api.utils.{Flattener, Produce}
import shapeless.HList

trait ParserBasedAPI[F[_], L[_], P[_], Base, Req] extends API[F, P, Base] {

  implicit def F: Functor[L]

  def requestFactory: Produce[Vector[String], Req]
  def executor: RequestExecutor.Aux[Req, L, Base]
  def flattener: Flattener[F, L, P]

  def get[R](implicit
             IR: IsResource[R],
             parser: SingleResourceParser.Aux[P, Base, R]): SingleReadResourceDefinition[F, P, R, Base] =
    new ParserBasedSingleReadResourceDefinition[F, L, P, R, Req, Base](requestFactory, executor, flattener, parser)

  def getAll[R <: HList](implicit IR: IsResource[R], parser: MultipleResourcesParser.Aux[P, Base, R]): MultipleReadResourceDefinition[F, P, R, Base] =
    new ParserBasedMultipleReadResourceDefinition[F, L, P, R, Req, Base](requestFactory, executor, flattener, parser)
}
