package ru.pavkin.todoist.api.core.command

import cats.FlatMap
import ru.pavkin.todoist.api.core.parser.SingleResponseDecoder
import ru.pavkin.todoist.api.core.{CommandReturns, RequestDefinition, ToRawRequest}
import shapeless._

trait SingleCommandDefinition[F[_], P[_], C, R, Base]
  extends RequestDefinition[F, P, R, Base] {

  def and[CC, RR](command: CC)
                 (implicit
                  FM: FlatMap[P],
                  tr: ToRawRequest[CC],
                  cr: CommandReturns.Aux[CC, RR],
                  parser: SingleResponseDecoder.Aux[P, Base, RR])
  : MultipleCommandDefinition[F, P, CC :: C :: HNil, RR :: R :: HNil, Base]
}


