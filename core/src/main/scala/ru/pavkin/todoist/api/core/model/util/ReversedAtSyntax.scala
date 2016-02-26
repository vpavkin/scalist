package ru.pavkin.todoist.api.core.model.util

import shapeless._
import shapeless.ops.hlist.{At, Reverse}

trait ReversedAtSyntax extends Nats {
  val _0: _0 = new _0

  implicit class ReversedAtHListOps[A <: HList](a: A) {
    def resultFor[R <: HList, Out](index: Nat)
                                  (implicit R: Reverse.Aux[A, R], A: At.Aux[R, index.N, Out]): Out = A(a.reverse)
  }
}
