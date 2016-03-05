package ru.pavkin.todoist.api.core.model.util

import shapeless._
import shapeless.ops.hlist.{At, Reverse}

trait ReversedAtSyntax extends Nats {
  val _0: _0 = new _0

  implicit class ReversedAtHListOps[A <: HList](a: A) {

    /**
      * Returns the result of the command that is located under specified index in the request
      *
      *  - For [[ru.pavkin.todoist.api.core.model.SimpleCommand]]
      * returns [[ru.pavkin.todoist.api.core.model.CommandResult]]
      *  - For [[ru.pavkin.todoist.api.core.model.TempIdCommand]]
      * returns [[ru.pavkin.todoist.api.core.model.TempIdCommandResult]]
      *
      * @param index the zero based command index, prefixed by an underscore, e.g. `_0` or `_12`
      * @example {{{res.resultFor(_0)}}}
      */
    def resultFor[R <: HList, Out](index: Nat)
                                  (implicit R: Reverse.Aux[A, R], A: At.Aux[R, index.N, Out]): Out = A(a.reverse)
  }
}
