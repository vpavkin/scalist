package ru.pavkin.todoist.api.dispatch.circe

import cats.data.Xor
import dispatch.Req
import io.circe.{Decoder, DecodingFailure, Json, JsonObject}
import ru.pavkin.todoist.api.Token
import ru.pavkin.todoist.api.circe.{CirceAPISuite, CirceDecoder}
import ru.pavkin.todoist.api.core._
import ru.pavkin.todoist.api.core.decoder.{SingleCommandResponseDecoder, SingleResponseDecoder}
import ru.pavkin.todoist.api.dispatch.core.DispatchAuthorizedRequestFactory
import ru.pavkin.todoist.api.dispatch.impl.circe.{DispatchAPI, DispatchJsonRequestExecutor}
import ru.pavkin.todoist.api.suite.{FutureBasedAPISuite, PlainAPISuite}
import shapeless._
import shapeless.tag.@@

import scala.concurrent.ExecutionContext

trait JsonAPI
  extends PlainAPISuite[DispatchAPI.Result, CirceDecoder.Result, Json]
    with CirceAPISuite[DispatchAPI.Result]
    with FutureBasedAPISuite[DispatchAPI.Result, CirceDecoder.Result, Json] {

  implicit def toRawRequest: ToRawRequest[Json] = ToRawRequest.command((json: Json) => List(json.noSpaces))

  implicit def labelledParser[T]: Decoder[Json @@ T] = Decoder[Json].map(a => tag[T](a))

  override implicit val projectsParser: SingleResponseDecoder.Aux[CirceDecoder.Result, Json, Projects] = projectsDecoder
  override implicit val labelsParser: SingleResponseDecoder.Aux[CirceDecoder.Result, Json, Labels] = labelsDecoder

  private def uuid(json: Json): Option[String] =
    json.asObject.flatMap(uuid)

  // todo: extract dto keys
  private def uuid(json: JsonObject): Option[String] =
    json("uuid").flatMap(_.asString)


  implicit val singleCRParser: SingleCommandResponseDecoder.Aux[CirceDecoder.Result, Json, Json, Json] =
    SingleCommandResponseDecoder.using[CirceDecoder.Result, Json, Json, Json] {
      (command: Json, result: Json) => {
        val decodingError = DecodingFailure(s"Failed to find result by uuid for command $command", Nil)
        Xor.fromOption(
          uuid(command).flatMap(commandUUID =>
            result.hcursor
              .downField("SyncStatus")
              .downField(commandUUID)
              .focus
          ),
          decodingError)
      }
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
