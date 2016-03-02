package ru.pavkin.todoist.api.circe

import cats.data.Xor
import io.circe.{Decoder, DecodingFailure, Json}
import ru.pavkin.todoist.api.circe.CirceDecoder._
import ru.pavkin.todoist.api.core.decoder.SingleResponseDecoder

object CirceDecoder {
  type Result[T] = Xor[DecodingFailure, T]
}

case class CirceDecoder[A](implicit D: Decoder[A]) extends SingleResponseDecoder[Result, Json, A] {

  def parse(resource: Json): Result[A] =
    D.decodeJson(resource)
}


