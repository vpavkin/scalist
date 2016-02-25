package ru.pavkin.todoist.api.core.command

import cats.{FlatMap, Functor}
import ru.pavkin.todoist.api.RawRequest
import ru.pavkin.todoist.api.core.CommandReturns.Aux
import ru.pavkin.todoist.api.core._
import ru.pavkin.todoist.api.core.decoder.SingleCommandResponseDecoder
import ru.pavkin.todoist.api.utils.{Flattener, Produce}
import shapeless.{::, HNil}

class SingleCommandRequestDefinition[F[_], L[_], P[_], C, R, Req, Base]
    (requestFactory: RawRequest Produce Req,
    executor: RequestExecutor.Aux[Req, L, Base],
    flattener: Flattener[F, L, P],
    parser: SingleCommandResponseDecoder.Aux[P, C, Base, R])
   (command: C)
   (implicit val trr: ToRawRequest[C],
    override implicit val F: Functor[L])
  extends CompositeExecutedRequestDefinition[F, L, P, R, Req, Base](
    requestFactory, executor, flattener
  ) with SingleCommandDefinition[F, P, C, R, Base] {

  def toRawRequest: RawRequest = trr.rawRequest(command)
  def parse(r: Base): P[R] = parser.parse(command)(r)

  def and[CC, RR](otherCommand: CC)
                 (implicit
                  FM: FlatMap[P],
                  tr: ToRawRequest[CC],
                  cr: Aux[CC, RR],
                  otherParser: SingleCommandResponseDecoder.Aux[P, CC, Base, RR])
  : MultipleCommandDefinition[F, P, CC :: C :: HNil, RR :: R :: HNil, Base] =
    new MultipleCommandRequestDefinition[F, L, P, CC :: C :: HNil, RR :: R :: HNil, Req, Base](
      requestFactory, executor, flattener, parser.combine(otherParser)
    )(otherCommand :: command :: HNil)
}
