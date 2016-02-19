package ru.pavkin.todoist.api.core

import shapeless._

trait CommandReturns[Command] {
  type Result
}

object CommandReturns {
  type Aux[C, R] = CommandReturns[C] {type Result = R}

  def apply[Command, Result](implicit ev: CommandReturns.Aux[Command, Result]): CommandReturns[Command] = ev

  implicit def single[C1, R1, C2, R2](implicit
                                      CR1: CommandReturns.Aux[C1, R1],
                                      CR2: CommandReturns.Aux[C2, R2])
  : CommandReturns.Aux[C2 :: C1 :: HNil, R2 :: R1 :: HNil] =
    new CommandReturns[C2 :: C1 :: HNil] {
      type Result = R2 :: R1 :: HNil
    }

  implicit def recurse[CH, RH, CT <: HList, RT <: HList](implicit
                                                         HCR: CommandReturns.Aux[CH, RH],
                                                         TCR: CommandReturns.Aux[CT, RT])
  : CommandReturns.Aux[CH :: CT, RH :: RT] =
    new CommandReturns[CH :: CT] {
      type Result = RH :: RT
    }
}
