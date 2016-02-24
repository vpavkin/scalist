package ru.pavkin.todoist.api.core

import ru.pavkin.todoist.api.core.command.{MultipleCommandDefinition, SingleCommandDefinition}
import ru.pavkin.todoist.api.core.decoder._
import ru.pavkin.todoist.api.core.query.{MultipleQueryDefinition, SingleQueryDefinition}
import ru.pavkin.todoist.api.utils.IsDistinctConstraint
import shapeless._
import shapeless.ops.hlist.Reverse

trait API[F[_], P[_], Base] {
  def get[R](implicit
             IR: HasRawRequest[R],
             parser: SingleResponseDecoder.Aux[P, Base, R]): SingleQueryDefinition[F, P, R, Base]

  def getAll[R <: HList](implicit
                         IR: HasRawRequest[R],
                         ID: IsDistinctConstraint[R],
                         parser: MultipleResponseDecoder.Aux[P, Base, R]): MultipleQueryDefinition[F, P, R, Base]

  def perform[C, R](command: C)
                   (implicit
                    trr: ToRawRequest[C],
                    cr: CommandReturns.Aux[C, R],
                    parser: SingleCommandResponseDecoder.Aux[P, C, Base, R]): SingleCommandDefinition[F, P, C, R, Base]

  def performAll[C <: HList, R <: HList, CR <: HList](commands: C)
                                                     (implicit
                                                      R: Reverse.Aux[C, CR],
                                                      trr: ToRawRequest[CR],
                                                      cr: CommandReturns.Aux[CR, R],
                                                      parser: MultipleCommandResponseDecoder.Aux[P, CR, Base, R])
  : MultipleCommandDefinition[F, P, CR, R, Base]
}







