package ru.pavkin.todoist.api.core.command

import cats.{FlatMap, Functor}
import ru.pavkin.todoist.api.RawRequest
import ru.pavkin.todoist.api.core.CommandReturns.Aux
import ru.pavkin.todoist.api.core._
import ru.pavkin.todoist.api.core.parser.{MultipleResourcesParser, SingleResourceParser}
import ru.pavkin.todoist.api.utils.{Flattener, Produce}
import shapeless.{::, HList}

class MultipleCommandRequestDefinition[F[_], L[_], P[_], C <: HList, R <: HList, Req, Base]
    (requestFactory: RawRequest Produce Req,
    executor: RequestExecutor.Aux[Req, L, Base],
    flattener: Flattener[F, L, P],
    parser: MultipleResourcesParser.Aux[P, Base, R])
   (commands: C)
   (implicit val trr: ToRawRequest[C],
    override implicit val F: Functor[L])

  extends ExecutedRequestDefinition[F, L, P, R, Req, Base]
    with MultipleCommandDefinition[F, P, C, R, Base] {

  def load: L[Base] = executor.execute(requestFactory.produce(trr.rawRequest(commands)))
  def flatten(r: L[P[R]]): F[R] = flattener.flatten(r)
  def parse(r: Base): P[R] = parser.parse(r)


  def and[CC, RR](otherCommand: CC)
                 (implicit
                  FM: FlatMap[P],
                  tr: ToRawRequest[CC],
                  cr: Aux[CC, RR],
                  otherParser: SingleResourceParser.Aux[P, Base, RR])
  : MultipleCommandDefinition[F, P, CC :: C, RR :: R, Base] =
    new MultipleCommandRequestDefinition[F, L, P, CC :: C, RR :: R, Req, Base](
      requestFactory, executor, flattener, parser.combine(otherParser)
    )(otherCommand :: commands)
}
