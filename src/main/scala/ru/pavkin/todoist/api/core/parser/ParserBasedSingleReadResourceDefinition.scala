package ru.pavkin.todoist.api.core.parser

import cats.{FlatMap, Functor}
import ru.pavkin.todoist.api.core._
import ru.pavkin.todoist.api.utils.{Produce, Flattener}
import shapeless.{::, <:!<, HNil}

class ParserBasedSingleReadResourceDefinition[F[_], L[_], P[_], R, Req, Base](requestFactory: Vector[String] Produce Req,
                                                                              executor: RequestExecutor.Aux[Req, L, Base],
                                                                              flattener: Flattener[F, L, P],
                                                                              parser: SingleResourceParser.Aux[P, Base, R])
                                                                             (override implicit val itr: IsResource[R],
                                                                              override implicit val F: Functor[L])
  extends ParsedBasedRequestDefinition[F, L, P, R, Req, Base] with SingleReadResourceDefinition[F, P, R, Base] {

  def load: L[Base] = executor.execute(requestFactory.produce(itr.strings))
  def flatten(r: L[P[R]]): F[R] = flattener.flatten(r)
  def parse(r: Base): P[R] = parser.parse(r)

  def and[RR](implicit
              FM: FlatMap[P],
              NEQ: <:!<[RR, R],
              ir: IsResource[RR],
              rrParser: SingleResourceParser.Aux[P, Base, RR]): MultipleReadResourceDefinition[F, P, RR :: R :: HNil, Base] =
    new ParserBasedMultipleReadResourceDefinition[F, L, P, RR :: R :: HNil, Req, Base](
      requestFactory, executor, flattener, parser.combine(rrParser)
    )
}
