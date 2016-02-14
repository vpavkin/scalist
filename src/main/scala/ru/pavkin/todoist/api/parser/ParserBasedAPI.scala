package ru.pavkin.todoist.api.parser

import cats.Functor
import ru.pavkin.todoist.api.core.ReadResourceType.All
import ru.pavkin.todoist.api.core._
import ru.pavkin.todoist.api.utils.{Produce, Flattener}
import shapeless.HList

trait ParserBasedAPI[F[_], L[_], P[_], Req] extends API[F] {

  implicit def F: Functor[L]

  def requestFactory: Produce[Vector[String], Req]
  def executor: RequestExecutor.Aux[Req, L, String]
  def flattener: Flattener[F, L, P]
  def parserProvider: ParserProvider[P, String]

  def get[T <: ReadResourceType](implicit ITR: IsResource[T]): SingleReadResourceDefinition[F, T, ITR.Repr] =
    new ParserBasedSingleReadResourceDefinition[F, L, P, T, ITR.Repr, Req](requestFactory, executor, flattener, parserProvider)(IsResource[T], F)

  def getAll[R <: HList](implicit ITR: IsResource.Aux[All, R]): MultipleReadResourceDefinition[F, All, R] =
    new ParserBasedMultipleReadResourceDefinition[F, L, P, All, R, Req](requestFactory, executor, flattener, parserProvider)

}
