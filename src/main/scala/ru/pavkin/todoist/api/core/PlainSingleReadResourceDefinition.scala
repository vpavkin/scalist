package ru.pavkin.todoist.api.core

import cats.{FlatMap, Id}
import ru.pavkin.todoist.api.parser.SingleResourceParser
import ru.pavkin.todoist.api.utils.Produce
import shapeless.{::, <:!<, HNil}

class PlainSingleReadResourceDefinition[F[_], R, Req, Base](requestFactory: Vector[String] Produce Req,
                                                            executor: RequestExecutor.Aux[Req, F, Base])
                                                           (override implicit val itr: IsResource[R])
  extends SingleReadResourceDefinition[F, Id, R, Base] {

  type Out = Base

  def and[RR](implicit
              F: FlatMap[Id],
              NEQ: <:!<[RR, R],
              ir: IsResource[RR],
              parser: SingleResourceParser.Aux[Id, Base, RR]): MultipleReadResourceDefinition[F, Id, RR :: R :: HNil, Base] =
    new PlainMultipleReadResourceDefinition[F, RR :: R :: HNil, Req, Base](requestFactory, executor)

  def execute: F[Out] = executor.execute(requestFactory.produce(itr.strings))
}
