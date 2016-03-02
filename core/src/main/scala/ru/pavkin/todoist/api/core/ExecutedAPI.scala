package ru.pavkin.todoist.api.core

import cats.Functor
import ru.pavkin.todoist.api.RawRequest
import ru.pavkin.todoist.api.core.CommandReturns.Aux
import ru.pavkin.todoist.api.core.command._
import ru.pavkin.todoist.api.core.decoder._
import ru.pavkin.todoist.api.core.query._
import ru.pavkin.todoist.api.utils.{IsDistinctConstraint, Flattener, Produce}
import shapeless.HList
import shapeless.ops.hlist.Reverse

trait ExecutedAPI[F[_], L[_], P[_], Req, Base] extends API[F, P, Base] {

  implicit def F: Functor[L]

  def requestFactory: Produce[RawRequest, Req]
  def executor: RequestExecutor.Aux[Req, L, Base]
  def flattener: Flattener[F, L, P]

  def get[R](implicit
             IR: HasRawRequest[R],
             parser: SingleResponseDecoder[P, Base, R]): SingleQueryDefinition[F, P, R, Base] =
    new SingleQueryRequestDefinition[F, L, P, R, Req, Base](requestFactory, executor, flattener, parser)

  def getAll[R <: HList](implicit
                         IR: HasRawRequest[R],
                         ID: IsDistinctConstraint[R],
                         parser: MultipleResponseDecoder[P, Base, R])
  : MultipleQueryDefinition[F, P, R, Base] =
    new MultipleQueryRequestDefinition[F, L, P, R, Req, Base](requestFactory, executor, flattener, parser)

  def perform[C, R](command: C)
                   (implicit
                    trr: ToRawRequest[C],
                    cr: Aux[C, R],
                    parser: SingleCommandResponseDecoder.Aux[P, C, Base, R])
  : SingleCommandDefinition[F, P, C, R, Base] =
    new SingleCommandRequestDefinition[F, L, P, C, R, Req, Base](requestFactory, executor, flattener, parser)(command)

  def performAll[C <: HList, R <: HList, CR <: HList](commands: C)
                                                     (implicit
                                                      R: Reverse.Aux[C, CR],
                                                      trr: ToRawRequest[CR],
                                                      cr: CommandReturns.Aux[CR, R],
                                                      parser: MultipleCommandResponseDecoder.Aux[P, CR, Base, R])
  : MultipleCommandDefinition[F, P, CR, R, Base] =
    new MultipleCommandRequestDefinition[F, L, P, CR, R, Req, Base](
      requestFactory, executor, flattener, parser
    )(R(commands))
}
