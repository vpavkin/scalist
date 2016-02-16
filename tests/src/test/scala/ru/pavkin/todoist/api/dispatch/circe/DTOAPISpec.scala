package ru.pavkin.todoist.api.dispatch.circe

import io.circe.Json
import org.scalatest.FunSuite
import org.scalatest.prop.Checkers
import ru.pavkin.todoist.api.circe.CirceDecoder
import ru.pavkin.todoist.api.core.{MultipleReadResourceDefinition, SingleReadResourceDefinition}
import ru.pavkin.todoist.api.dispatch.circe.dto._
import ru.pavkin.todoist.api.dispatch.impl.circe.model.DispatchModelAPI
import shapeless.test.{illTyped, typed}
import shapeless.{::, HNil}

class DTOAPISpec extends FunSuite with Checkers {

  test("Dispatch circe json API") {
    val api = todoist.authorize("token")
    typed[SingleReadResourceDefinition[DispatchModelAPI.Result, CirceDecoder.Result, Projects, Json]](
      api.get[Projects]
    )
    typed[MultipleReadResourceDefinition[DispatchModelAPI.Result, CirceDecoder.Result, Labels :: Projects :: HNil, Json]](
      api.get[Projects].and[Labels]
    )
    typed[MultipleReadResourceDefinition[DispatchModelAPI.Result, CirceDecoder.Result, Projects :: Labels :: HNil, Json]](
      api.getAll[All]
    )
    typed[MultipleReadResourceDefinition[DispatchModelAPI.Result, CirceDecoder.Result, Labels :: Projects :: HNil, Json]](
      api.getAll[Labels :: Projects :: HNil]
    )
    typed[MultipleReadResourceDefinition[DispatchModelAPI.Result, CirceDecoder.Result, Labels :: Projects :: HNil, Json]](
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

