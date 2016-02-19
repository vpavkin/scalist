package ru.pavkin.todoist.api.dispatch.circe

import cats.data.Xor
import io.circe.Json
import ru.pavkin.todoist.api.circe.CirceDecoder
import ru.pavkin.todoist.api.core.FutureBasedAPISuiteSpec
import ru.pavkin.todoist.api.core.command.{SingleCommandDefinition, MultipleCommandDefinition}
import ru.pavkin.todoist.api.dispatch.impl.circe.DispatchAPI
import shapeless.{HNil, ::}
import shapeless.test.typed

import scala.concurrent.ExecutionContext.Implicits.global

class JsonAPISpec
  extends FutureBasedAPISuiteSpec[DispatchAPI.Result, CirceDecoder.Result, Json]("Dispatch Circe Json API")
    with JsonAPI {

  test(s"Dispatch Circe Json API commands") {
    val api = todoist.withToken("token")
    typed[SingleCommandDefinition[DispatchAPI.Result, CirceDecoder.Result, Json, Json, Json]](
      api.perform(Json.obj())
    )
    typed[MultipleCommandDefinition[DispatchAPI.Result,
      CirceDecoder.Result,
      Json :: Json :: HNil,
      Json :: Json :: HNil,
      Json]](
      api.perform(Json.obj()).and(Json.array())
    )

    typed[MultipleCommandDefinition[DispatchAPI.Result,
      CirceDecoder.Result,
      Json :: Json :: Json :: HNil,
      Json :: Json :: Json :: HNil,
      Json]](
      api.performAll(Json.obj() :: Json.array() :: Json.array() :: HNil)
    )
  }

  test(s"Dispatch Circe Json API parser") {
    def t(command: Json, result: Json, condition: CirceDecoder.Result[Json] => Boolean): Unit =
      condition(singleCRParser.parse(command)(result)) shouldBe true

    t(Json.obj("uuid" -> Json.string("123")),
      Json.obj("SyncStatus" -> Json.obj("123" -> Json.string("ok"))),
      _ == Xor.Right(Json.string("ok"))
    )

    t(Json.obj("uuid" -> Json.string("123")),
      Json.obj("SyncStatus" -> Json.obj("123" -> Json.obj("error" -> Json.string("msg")))),
      _ == Xor.Right(Json.obj("error" -> Json.string("msg")))
    )

    t(Json.obj("uuid" -> Json.string("123")),
      Json.empty,
      _.isLeft
    )

    t(Json.obj("uuid" -> Json.string("123")),
      Json.obj("SyncStatus" -> Json.obj("aaa" -> Json.string("ok"))),
      _.isLeft
    )

    t(Json.obj("something" -> Json.string("123")),
      Json.obj("SyncStatus" -> Json.obj("aaa" -> Json.string("ok"))),
      _.isLeft
    )
  }
}
