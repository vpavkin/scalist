package ru.pavkin.todoist.api.dispatch.circe

import cats.data.Xor
import dispatch.Req
import io.circe.{DecodingFailure, Decoder, Json}
import ru.pavkin.todoist.api.Token
import ru.pavkin.todoist.api.circe.{CirceAPISuite, CirceDecoder}
import ru.pavkin.todoist.api.core._
import ru.pavkin.todoist.api.core.decoder.{MultipleResponseDecoder, SingleResponseDecoder}
import ru.pavkin.todoist.api.dispatch.core.DispatchAuthorizedRequestFactory
import ru.pavkin.todoist.api.dispatch.impl.circe.{DispatchAPI, DispatchJsonRequestExecutor}
import ru.pavkin.todoist.api.suite.{FutureBasedAPISuite, PlainAPISuite}
import shapeless.ops.hlist.{Length, Fill, Unifier}
import shapeless.ops.nat.ToInt
import shapeless.tag.@@
import shapeless._

import scala.concurrent.ExecutionContext
import scala.util.Try

trait JsonAPI
  extends PlainAPISuite[DispatchAPI.Result, CirceDecoder.Result, Json]
    with CirceAPISuite[DispatchAPI.Result]
    with FutureBasedAPISuite[DispatchAPI.Result, CirceDecoder.Result, Json] {

  implicit def toRawRequest: ToRawRequest[Json] = ToRawRequest((json: Json) => Vector(json.noSpaces))

  implicit def labelledParser[T]: Decoder[Json @@ T] = Decoder[Json].map(a => tag[T](a))

  override implicit val projectsParser: SingleResponseDecoder.Aux[CirceDecoder.Result, Json, Projects] = projectsDecoder
  override implicit val labelsParser: SingleResponseDecoder.Aux[CirceDecoder.Result, Json, Labels] = labelsDecoder

  implicit val singleCRParser: SingleResponseDecoder.Aux[CirceDecoder.Result, Json, Json] =
    SingleResponseDecoder.using[CirceDecoder.Result, Json, Json] {
      Xor.right
    }

  implicit val singleCRParser1: MultipleResponseDecoder.Aux[CirceDecoder.Result, Json :: HNil, Json :: HNil] =
    MultipleResponseDecoder.using[CirceDecoder.Result, Json :: HNil, Json :: HNil] {
      l => Xor.fromOption(
        l.head.asArray.flatMap(_.headOption),
        DecodingFailure("Couldn't find command result at index 0", Nil)
      ).map(_ :: HNil)
    }

  implicit def multipleCRParser[H, T <: HList, N <: Nat](implicit
                                                         ev: MultipleResponseDecoder.Aux[CirceDecoder.Result, T, T],
                                                         toInt: ToInt[N],
                                                         length: Length.Aux[T, N])
  : MultipleResponseDecoder.Aux[CirceDecoder.Result, Json :: T, Json :: T] =
    MultipleResponseDecoder.using[CirceDecoder.Result, Json :: T, Json :: T] {
      l => Xor.fromOption(
        l.head.asArray.flatMap(jl => Try(jl(toInt())).toOption),
        DecodingFailure(s"Couldn't find command result at index ${toInt()}", Nil)
      ).flatMap(j => ev.parse(l.tail).map(j :: _))
    }


  def todoist(implicit ec: ExecutionContext): UnauthorizedAPI[DispatchAPI.Result, CirceDecoder.Result, Json] =
    new UnauthorizedAPI[DispatchAPI.Result, CirceDecoder.Result, Json] {
      private lazy val executor: RequestExecutor.Aux[Req, DispatchJsonRequestExecutor.Result, Json] =
        new DispatchJsonRequestExecutor

      def withToken(token: Token): API[DispatchAPI.Result, CirceDecoder.Result, Json] =
        new DispatchAPI(
          new DispatchAuthorizedRequestFactory(token),
          executor
        )
    }
}
