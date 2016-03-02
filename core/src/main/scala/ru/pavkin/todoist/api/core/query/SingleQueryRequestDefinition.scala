package ru.pavkin.todoist.api.core.query

import cats.{FlatMap, Functor}
import ru.pavkin.todoist.api.RawRequest
import ru.pavkin.todoist.api.core._
import ru.pavkin.todoist.api.core.decoder.SingleResponseDecoder
import ru.pavkin.todoist.api.utils.{Flattener, Produce}
import shapeless.{::, <:!<, HNil}

class SingleQueryRequestDefinition[F[_], L[_], P[_], R, Req, Base](requestFactory: RawRequest Produce Req,
                                                                   executor: RequestExecutor.Aux[Req, L, Base],
                                                                   flattener: Flattener[F, L, P],
                                                                   parser: SingleResponseDecoder[P, Base, R])
                                                                  (implicit val itr: HasRawRequest[R],
                                                                   override implicit val F: Functor[L])
  extends CompositeExecutedRequestDefinition[F, L, P, R, Req, Base](
    requestFactory, executor, flattener
  ) with SingleQueryDefinition[F, P, R, Base] {

  def toRawRequest: RawRequest = itr.rawRequest
  def parse(r: Base): P[R] = parser.parse(r)

  def and[RR](implicit
              FM: FlatMap[P],
              NEQ: <:!<[RR, R],
              ir: HasRawRequest[RR],
              rrParser: SingleResponseDecoder[P, Base, RR])
  : MultipleQueryDefinition[F, P, RR :: R :: HNil, Base] =
    new MultipleQueryRequestDefinition[F, L, P, RR :: R :: HNil, Req, Base](
      requestFactory, executor, flattener, parser.combine(rrParser)
    )
}
