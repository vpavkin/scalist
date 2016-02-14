package ru.pavkin.todoist.api.core

import ru.pavkin.todoist.api.utils.{Produce, NotContains}
import shapeless.{::, HList, LUBConstraint}

class PlainMultipleReadResourceDefinition[F[_], T <: HList, R <: HList, Req, Res0](requestFactory: Vector[String] Produce Req,
                                                                                   executor: RequestExecutor.Aux[Req, F, Res0])
                                                                                  (override implicit val lub: LUBConstraint[T, ReadResourceType],
                                                                                   override implicit val itr: IsResource.Aux[T, R])
  extends MultipleReadResourceDefinition[F, T, R] {

  type Res = Res0


  def and[TT <: ReadResourceType](implicit NC: NotContains[T, TT],
                                  ITR: IsResource[TT]): MultipleReadResourceDefinition[F, TT :: T, ITR.Repr :: R] =
    new PlainMultipleReadResourceDefinition[F, TT :: T, ITR.Repr :: R, Req, Res0](requestFactory, executor)

  def execute: F[Res0] = executor.execute(requestFactory.produce(itr.strings))
}
