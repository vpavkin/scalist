package ru.pavkin.todoist.api.core.command

import cats.FlatMap
import ru.pavkin.todoist.api.core.decoder.{SingleCommandResponseDecoder, SingleResponseDecoder}
import ru.pavkin.todoist.api.core.{CommandReturns, RequestDefinition, ToRawRequest}
import shapeless._

/**
  * A definition of a single command request.
  *
  * Call `execute` to perform the command and get the result (under the effect)
  */
trait SingleCommandDefinition[F[_], P[_], C, R, Base]
  extends RequestDefinition[F, P, R, Base] {

  /**
    * Returns a new command request definition, that after execution will
    * execute both commands and return an `HList` of corresponding results
    *
    * Equivalent of calling:
    * {{{api.performAll(command1 :+ command2)}}}
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
  : MultipleCommandDefinition[F, P, CC :: C :: HNil, RR :: R :: HNil, Base]
}


