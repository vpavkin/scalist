package ru.pavkin.todoist.api.core

import cats.Functor
import ru.pavkin.todoist.api.RawRequest
import ru.pavkin.todoist.api.core.CommandReturns.Aux
import ru.pavkin.todoist.api.core.command._
import ru.pavkin.todoist.api.core.parser.{SingleResourceParser, MultipleResourcesParser}
import ru.pavkin.todoist.api.core.query._
import ru.pavkin.todoist.api.utils.{Flattener, Produce}
import shapeless.HList

trait ExecutedAPI[F[_], L[_], P[_], Req, Base] extends API[F, P, Base] {

  implicit def F: Functor[L]

  def requestFactory: Produce[RawRequest, Req]
  def executor: RequestExecutor.Aux[Req, L, Base]
  def flattener: Flattener[F, L, P]

  def get[R](implicit
             IR: HasRawRequest[R],
             parser: SingleResourceParser.Aux[P, Base, R]): SingleQueryDefinition[F, P, R, Base] =
    new SingleQueryRequestDefinition[F, L, P, R, Req, Base](requestFactory, executor, flattener, parser)

  def getAll[R <: HList](implicit
                         IR: HasRawRequest[R],
                         parser: MultipleResourcesParser.Aux[P, Base, R])
  : MultipleQueryDefinition[F, P, R, Base] =
    new MultipleQueryRequestDefinition[F, L, P, R, Req, Base](requestFactory, executor, flattener, parser)

  def perform[C, R](command: C)
                   (implicit
                    trr: ToRawRequest[C],
                    cr: Aux[C, R],
                    parser: SingleResourceParser.Aux[P, Base, R]): SingleCommandDefinition[F, P, C, R, Base] =
    new SingleCommandRequestDefinition[F, L, P, C, R, Req, Base](requestFactory, executor, flattener, parser)(command)

  def performAll[C <: HList, R <: HList](commands: C)
                                        (implicit
                                         trr: ToRawRequest[C],
                                         cr: Aux[C, R],
                                         parser: MultipleResourcesParser.Aux[P, Base, R])
  : MultipleCommandDefinition[F, P, C, R, Base] =
    new MultipleCommandRequestDefinition[F, L, P, C, R, Req, Base](
      requestFactory, executor, flattener, parser
    )(commands)
}
