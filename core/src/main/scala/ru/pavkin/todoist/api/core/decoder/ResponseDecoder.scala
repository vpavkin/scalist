package ru.pavkin.todoist.api.core.decoder

trait ResponseDecoder[F[_], Base] {self =>
  type Out
  def parse(resource: Base): F[Out]
}

object ResponseDecoder {
  type Aux[F[_], Base, Out0] = ResponseDecoder[F, Base] {type Out = Out0}
  def apply[F[_], Base, Out](implicit ev: Aux[F, Base, Out]): ResponseDecoder[F, Base] = ev
}
