package ru.pavkin.todoist.api.dispatch.circe

import cats._
import io.circe.Json
import org.scalatest.FunSuite
import org.scalatest.prop.Checkers
import ru.pavkin.todoist.api.core.{MultipleReadResourceDefinition, SingleReadResourceDefinition}
import ru.pavkin.todoist.api.dispatch.circe.json._
import ru.pavkin.todoist.api.dispatch.impl.circe.json.DispatchJsonRequestExecutor
import shapeless.test.{illTyped, typed}
import shapeless.{::, HNil}

class JsonAPISpec extends FunSuite with Checkers {

  test("Dispatch circe json API") {
    val api = todoist.authorize("token")
    typed[SingleReadResourceDefinition[DispatchJsonRequestExecutor.Result, Id, Projects, Json]](
      api.get[Projects]
    )
    typed[MultipleReadResourceDefinition[DispatchJsonRequestExecutor.Result, Id, Labels :: Projects :: HNil, Json]](
      api.get[Projects].and[Labels]
    )
    typed[MultipleReadResourceDefinition[DispatchJsonRequestExecutor.Result, Id, Projects :: Labels :: HNil, Json]](
      api.getAll[All]
    )
    typed[MultipleReadResourceDefinition[DispatchJsonRequestExecutor.Result, Id, Labels :: Projects :: HNil, Json]](
      api.getAll[Labels :: Projects :: HNil]
    )
    typed[MultipleReadResourceDefinition[DispatchJsonRequestExecutor.Result, Id, Labels :: Projects :: HNil, Json]](
      api.getAll[Projects :: HNil].and[Labels]
    )

    illTyped("""api.get[String]""")
    illTyped("""api.get""")
    illTyped("""api.get[Projects].and[Int]""")
    illTyped("""api.get[Projects].and[Projects]""")
    illTyped("""api.get[Projects].and[Labels].and[Projects]""")
    illTyped("""api.getAll[Labels :: Projects :: HNil].and[Labels]""")
  }
}

