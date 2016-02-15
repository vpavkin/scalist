package ru.pavkin.todoist.api.core.plain

import cats.Id
import ru.pavkin.todoist.api.core._
import ru.pavkin.todoist.api.core.parser.MultipleResourcesParser.Aux
import ru.pavkin.todoist.api.core.parser.SingleResourceParser
import ru.pavkin.todoist.api.utils.Produce
import shapeless.HList

trait PlainAPI[F[_], Req, Base] extends API[F, Id, Base] {

  def requestFactory: Produce[Vector[String], Req]
  def executor: RequestExecutor.Aux[Req, F, Base]

  def get[R](implicit
             IR: IsResource[R],
             parser: SingleResourceParser.Aux[Id, Base, R]): SingleReadResourceDefinition[F, Id, R, Base] =
    new PlainSingleReadResourceDefinition[F, R, Req, Base](requestFactory, executor)

  def getAll[R <: HList](implicit
                         IR: IsResource[R],
                         parser: Aux[Id, Base, R]): MultipleReadResourceDefinition[F, Id, R, Base] =
    new PlainMultipleReadResourceDefinition[F, R, Req, Base](requestFactory, executor)
}
