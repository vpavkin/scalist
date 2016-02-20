package ru.pavkin.todoist.api.circe.decoders

import cats.data.Xor
import io.circe.Decoder._
import io.circe.generic.decoding.DerivedDecoder
import io.circe.{DecodingFailure, Decoder, HCursor}
import shapeless._

// todo: tests
trait PlainCoproductDecoder {

  implicit val decodeCNil: Decoder[CNil] =
    new DerivedDecoder[CNil] {
      final def apply(c: HCursor): Decoder.Result[CNil] =
        Xor.left(DecodingFailure("CNil", c.history))
    }

  implicit def plainCoproductDecoder[H, T <: Coproduct]
  (implicit
   decodeHead: Decoder[H],
   decodeTail: Decoder[T]): Decoder[H :+: T] = new Decoder[H :+: T] {
    def apply(c: HCursor): Result[H :+: T] =
      decodeTail(c).map(Inr(_)).orElse(decodeHead(c).map(Inl(_)))
  }

}
