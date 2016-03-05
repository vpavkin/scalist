package ru.pavkin.todoist.api.core.command

import cats.FlatMap
import ru.pavkin.todoist.api.core.decoder.{SingleCommandResponseDecoder, SingleResponseDecoder}
import ru.pavkin.todoist.api.core.{CommandReturns, ToRawRequest, RequestDefinition}
import shapeless._

/**
  * A definition of a multiple commands request.
  *
  * Call `execute` to perform the commands and get the results `HList` (under the effect)
  */
trait MultipleCommandDefinition[F[_], P[_], C <: HList, R <: HList, Base]
  extends RequestDefinition[F, P, R, Base] {

  /**
    * Returns a new command request definition, that after execution will
    * execute all the commands from this definition plus the added one
    * and return an `HList` of corresponding results
    *
    * See [[ru.pavkin.todoist.api.core.AuthorizedAPI.performAll]] for details on working with
    * multiple commands response
    */
  def and[CC, RR](command: CC)
                 (implicit
                  FM: FlatMap[P],
                  tr: ToRawRequest[CC],
                  cr: CommandReturns.Aux[CC, RR],
                  parser: SingleCommandResponseDecoder.Aux[P, CC, Base, RR])
  : MultipleCommandDefinition[F, P, CC :: C, RR :: R, Base]
}
