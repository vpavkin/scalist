package ru.pavkin.todoist.api.core

import cats.{FlatMap, Id}
import ru.pavkin.todoist.api.parser.SingleResourceParser
import ru.pavkin.todoist.api.utils.{Produce, NotContains}
import shapeless.{::, HList}

class PlainMultipleReadResourceDefinition[F[_], R <: HList, Req, Base](requestFactory: Vector[String] Produce Req,
                                                                       executor: RequestExecutor.Aux[Req, F, Base])
                                                                      (override implicit val itr: IsResource[R])
  extends MultipleReadResourceDefinition[F, Id, R, Base] {

  type Out = Base

  def and[RR](implicit
              F: FlatMap[Id],
              NC: NotContains[R, RR],
              ir: IsResource[RR],
              parser: SingleResourceParser.Aux[Id, Base, RR]): MultipleReadResourceDefinition[F, Id, RR :: R, Base] =
    new PlainMultipleReadResourceDefinition[F, RR :: R, Req, Base](requestFactory, executor)

  def execute: F[Out] = executor.execute(requestFactory.produce(itr.strings))
}
