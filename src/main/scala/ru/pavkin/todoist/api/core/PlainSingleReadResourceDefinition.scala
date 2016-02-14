package ru.pavkin.todoist.api.core

import ru.pavkin.todoist.api.utils.Produce
import shapeless.{::, <:!<, HNil}

class PlainSingleReadResourceDefinition[F[_], T <: ReadResourceType, R, Req, Res0](requestFactory: Vector[String] Produce Req,
                                                                                   executor: RequestExecutor.Aux[Req, F, Res0])
                                                                                  (override implicit val itr: IsResource.Aux[T, R])
  extends SingleReadResourceDefinition[F, T, R] {

  type Res = Res0

  def and[TT <: ReadResourceType](implicit NEQ: <:!<[TT, T], ITR: IsResource[TT]): MultipleReadResourceDefinition[F, TT :: T :: HNil, ITR.Repr :: R :: HNil] =
    new PlainMultipleReadResourceDefinition[F, TT :: T :: HNil, ITR.Repr :: R :: HNil, Req, Res0](requestFactory, executor)

  def execute: F[Res0] = executor.execute(requestFactory.produce(itr.strings))
}
