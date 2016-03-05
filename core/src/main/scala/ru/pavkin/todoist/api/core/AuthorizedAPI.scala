package ru.pavkin.todoist.api.core

import ru.pavkin.todoist.api.core.command.{MultipleCommandDefinition, SingleCommandDefinition}
import ru.pavkin.todoist.api.core.decoder._
import ru.pavkin.todoist.api.core.query.{MultipleQueryDefinition, SingleQueryDefinition}
import ru.pavkin.todoist.api.utils.IsDistinctConstraint
import shapeless._
import shapeless.ops.hlist.Reverse

/**
  * Authorized API client that is entitled to perform most of the API calls.
  *
  * Methods are separated into two groups:
  *
  * - `get` methods, for querying resources
  * - `perform` methods, for sending commands
  *
  * All the implicit parameters for those methods are usually supplied by the imported API suite, e.g.:
  *
  * {{{
  *  import ru.pavkin.todoist.api.dispatch.circe.default._
  * }}}
  *
  * All methods return request definition containers, that are not yet executed.
  * To execute a request definition, you have to call `execute` method on it.
  *
  * Request definitions can be chained together to compose a multiple entity request.
  * Chaining is done with `and` method, e.g.:
  *
  * {{{
  *  api.get[Projects].and[Labels].and[Tasks]
  *  api.getAll[Projects :: Labels :: HNil].and[Tasks]
  *  api.perform(AddProject("p1")).and(AddProject("p2"))
  *  api.performAll(AddProject("p1") :+ AddProject("p2"))
  * }}}
  */
trait AuthorizedAPI[F[_], P[_], Base] {

  /**
    * Returns a single resource request definition, that after being executed will return
    * the resource of type `R`
    */
  def get[R](implicit
             IR: HasRawRequest[R],
             parser: SingleResponseDecoder[P, Base, R]): SingleQueryDefinition[F, P, R, Base]

  /**
    * Returns a multiple resources request definition, that after being executed will return
    * an `HList` of resources, specified in phantom type parameter `R`
    *
    * Example usage:
    * {{{
    *  api.getAll[Projects :: Labels :: Tasks :: HNil]
    *  // will return List[Projects] :: List[Labels] :: List[Tasks] :: HNil upon execution
    * }}}
    *
    * Syntax helpers are available for working with multiple resources response.
    * After handling the API effect, you can call these methods on the result:
    *
    * {{{
    * res.projects // returns List[Project]
    * res.labels // returns List[Label]
    * // ...
    * // etc, but only for resources that were requested
    * }}}
    *
    * For syntax helpers to be available, you should import the syntax toolkit, for example:
    *
    * {{{
    *  import ru.pavkin.todoist.api.dispatch.circe.default.syntax._
    * }}}
    *
    * @note Doesn't allow to specify duplicate resources.
    */
  def getAll[R <: HList](implicit
                         IR: HasRawRequest[R],
                         ID: IsDistinctConstraint[R],
                         parser: MultipleResponseDecoder[P, Base, R]): MultipleQueryDefinition[F, P, R, Base]

  /** Returns a single command request definition, that when being executed
    * performs a supplied `command: C` and returns command result of type `R`.
    *
    * All command results are successors of [[ru.pavkin.todoist.api.core.model.TodoistCommandResult]]:
    *
    * - For [[ru.pavkin.todoist.api.core.model.SimpleCommand]]
    * returns [[ru.pavkin.todoist.api.core.model.CommandResult]]
    * - For [[ru.pavkin.todoist.api.core.model.TempIdCommand]]
    * returns [[ru.pavkin.todoist.api.core.model.TempIdCommandResult]]
    *
    * @param command Command to execute within the request
    */
  def perform[C, R](command: C)
                   (implicit
                    trr: ToRawRequest[C],
                    cr: CommandReturns.Aux[C, R],
                    parser: SingleCommandResponseDecoder.Aux[P, C, Base, R]): SingleCommandDefinition[F, P, C, R, Base]

  /**
    * Returns a multiple commands request definition, that when being executed
    * performs all supplied `commands` and returns an `HList` of corresponding command results.
    * See [[AuthorizedAPI.perform]] method docs for command results details
    *
    * Syntax helpers are available for multiple command results response.
    * After handling the API effect, you can call these methods on the result:
    *
    * {{{
    * res.resultFor(_0) // returns strictly typed result of the first command on the list
    * res.resultFor(_1) // returns strictly typed result of the seconds command on the list
    * // ...
    * // and so on, but only for the amount of commands that was actually sent
    *
    * res.resultFor(uuid:UUID) // tries to find result for command with specific uuid
    * // returns an Option[TodoistCommandResult]
    * }}}
    *
    * For syntax helpers to be available, you should import the syntax toolkit, for example:
    *
    * {{{
    *  import ru.pavkin.todoist.api.dispatch.circe.default.syntax._
    * }}}
    *
    * @param commands `HList` of commands to execute
    */
  def performAll[C <: HList, R <: HList, CR <: HList](commands: C)
                                                     (implicit
                                                      R: Reverse.Aux[C, CR],
                                                      trr: ToRawRequest[CR],
                                                      cr: CommandReturns.Aux[CR, R],
                                                      parser: MultipleCommandResponseDecoder.Aux[P, CR, Base, R])
  : MultipleCommandDefinition[F, P, CR, R, Base]
}







