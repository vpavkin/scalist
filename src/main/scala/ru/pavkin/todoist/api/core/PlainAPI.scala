package ru.pavkin.todoist.api.core

import ru.pavkin.todoist.api.core.IsResource._
import ru.pavkin.todoist.api.core.ReadResourceType._
import ru.pavkin.todoist.api.utils.Produce
import shapeless.HList

trait PlainAPI[F[_], Req, Res] extends API[F] {

  def requestFactory: Produce[Vector[String], Req]
  def executor: RequestExecutor.Aux[Req, F, Res]

  def get[T <: ReadResourceType](implicit ITR: IsResource[T]): SingleReadResourceDefinition[F, T, ITR.Repr] =
    new PlainSingleReadResourceDefinition[F, T, ITR.Repr, Req, Res](requestFactory, executor)(IsResource[T])

  def getAll[R <: HList](implicit ITR: Aux[All, R]): MultipleReadResourceDefinition[F, All, R] =
    new PlainMultipleReadResourceDefinition[F, All, R, Req, Res](requestFactory, executor)

}
