package ru.pavkin.todoist.api.core.decoder

trait ResponseDecoder[F[_], Base, Out] {self =>
  def parse(resource: Base): F[Out]
}

object ResponseDecoder {
  def apply[F[_], Base, Out](implicit ev: ResponseDecoder[F, Base, Out]): ResponseDecoder[F, Base, Out] = ev
}
