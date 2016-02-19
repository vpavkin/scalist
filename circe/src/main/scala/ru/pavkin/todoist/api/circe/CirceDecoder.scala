package ru.pavkin.todoist.api.circe

import cats.data.Xor
import cats.syntax.xor._
import io.circe.{Decoder, DecodingFailure, Json}
import ru.pavkin.todoist.api.circe.CirceDecoder._
import ru.pavkin.todoist.api.core.decoder.SingleResponseDecoder

object CirceDecoder {
  type Result[T] = Xor[DecodingFailure, T]
  type Locator = Json => Option[Json]
}

case class CirceDecoder[A](locator: Locator)
                          (implicit D: Decoder[A]) extends SingleResponseDecoder[Result, Json] {

  type Out = A

  def parse(resource: Json): Result[Out] =
    locator(resource)
      .map(D.decodeJson)
      .getOrElse(io.circe.DecodingFailure("CirceDecoder couldn't locate the resource", Nil).left)
}


