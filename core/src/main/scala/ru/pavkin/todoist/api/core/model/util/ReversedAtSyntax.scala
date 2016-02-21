package ru.pavkin.todoist.api.core.model.util

import shapeless._
import shapeless.ops.hlist.{At, Reverse}

trait ReversedAtSyntax extends Nats {
  val _0: _0 = new _0

  implicit class ReversedAtHListOps[A <: HList](a: A) {
    def resultFor[R <: HList](index: Nat)(implicit R: Reverse.Aux[A, R], A: At[R, index.N]): A.Out = A(a.reverse)
  }
}
