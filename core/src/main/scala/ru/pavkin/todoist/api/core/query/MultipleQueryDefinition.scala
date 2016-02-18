package ru.pavkin.todoist.api.core.query

import cats.{FlatMap, Functor}
import ru.pavkin.todoist.api.RawRequest
import ru.pavkin.todoist.api.core._
import ru.pavkin.todoist.api.core.parser.{MultipleResourcesParser, SingleResourceParser}
import ru.pavkin.todoist.api.utils.{Flattener, NotContains, Produce}
import shapeless.{::, HList}

class MultipleQueryDefinition[F[_], L[_], P[_], R <: HList, Req, Base](
                                          requestFactory: RawRequest Produce Req,
                                          executor: RequestExecutor.Aux[Req, L, Base],
                                          flattener: Flattener[F, L, P],
                                          parser: MultipleResourcesParser.Aux[P, Base, R])
                                                                      (implicit val itr: HasRawRequest[R],
                                          override implicit val F: Functor[L])
  extends ExecutedRequestDefinition[F, L, P, R, Req, Base]
    with MultipleReadResourceDefinition[F, P, R, Base] {

  def load: L[Base] = executor.execute(requestFactory.produce(itr.rawRequest))
  def flatten(r: L[P[R]]): F[R] = flattener.flatten(r)
  def parse(r: Base): P[R] = parser.parse(r)


  def and[RR](implicit
              FM: FlatMap[P],
              NC: NotContains[R, RR],
              ir: HasRawRequest[RR],
              rrParser: SingleResourceParser.Aux[P, Base, RR]): MultipleReadResourceDefinition[F, P, ::[RR, R], Base] =
    new MultipleQueryDefinition[F, L, P, RR :: R, Req, Base](
      requestFactory, executor, flattener, parser.combine(rrParser)
    )
}
