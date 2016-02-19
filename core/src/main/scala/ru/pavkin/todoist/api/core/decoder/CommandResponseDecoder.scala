package ru.pavkin.todoist.api.core.decoder

trait CommandResponseDecoder[F[_], Command, Base] {self =>
  type Out
  def parse(command: Command)(resource: Base): F[Out]
}

object CommandResponseDecoder {
  type Aux[F[_], Command, Base, Out0] = CommandResponseDecoder[F, Command, Base] {type Out = Out0}

  def apply[F[_], Command, Base, Out]
  (implicit ev: Aux[F, Command, Base, Out]): CommandResponseDecoder[F, Command, Base] = ev
}
