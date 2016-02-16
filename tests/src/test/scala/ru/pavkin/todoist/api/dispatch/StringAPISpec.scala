package ru.pavkin.todoist.api.dispatch

import cats._
import org.scalatest.FunSuite
import org.scalatest.prop.Checkers
import ru.pavkin.todoist.api.core.{MultipleReadResourceDefinition, SingleReadResourceDefinition}
import ru.pavkin.todoist.api.dispatch.impl.string.DispatchStringRequestExecutor
import ru.pavkin.todoist.api.dispatch.string._
import shapeless.test.{illTyped, typed}
import shapeless.{::, HNil}

class StringAPISpec extends FunSuite with Checkers {

  test("Dispatch string API") {
    val api = todoist.authorize("token")
    typed[SingleReadResourceDefinition[DispatchStringRequestExecutor.Result, Id, Projects, String]](
      api.get[Projects]
    )
    typed[MultipleReadResourceDefinition[DispatchStringRequestExecutor.Result, Id, Labels :: Projects :: HNil, String]](
      api.get[Projects].and[Labels]
    )
    typed[MultipleReadResourceDefinition[DispatchStringRequestExecutor.Result, Id, Projects :: Labels :: HNil, String]](
      api.getAll[All]
    )
    typed[MultipleReadResourceDefinition[DispatchStringRequestExecutor.Result, Id, Labels :: Projects :: HNil, String]](
      api.getAll[Labels :: Projects :: HNil]
    )
    typed[MultipleReadResourceDefinition[DispatchStringRequestExecutor.Result, Id, Labels :: Projects :: HNil, String]](
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

