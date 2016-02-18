package ru.pavkin.todoist.api.core

import cats._
import org.scalatest.FunSuite
import org.scalatest.prop.Checkers
import ru.pavkin.todoist.api.suite.FutureBasedAPISuite
import shapeless.test._
import shapeless.{::, HNil}

import scala.concurrent.ExecutionContext

abstract class FutureBasedAPISuiteSpec[F[_] : Apply, P[_] : FlatMap, Base](apiName: String)
                                                                          (implicit ec: ExecutionContext)
  extends FunSuite
    with Checkers
    with FutureBasedAPISuite[F, P, Base] {

  test(s"$apiName test suite") {
    val api = todoist.withToken("token")
    typed[SingleReadResourceDefinition[F, P, Projects, Base]](
      api.get[Projects]
    )
    typed[MultipleReadResourceDefinition[F, P, Labels :: Projects :: HNil, Base]](
      api.get[Projects].and[Labels]
    )
    typed[MultipleReadResourceDefinition[F, P, Projects :: Labels :: HNil, Base]](
      api.getAll[All]
    )
    typed[MultipleReadResourceDefinition[F, P, Labels :: Projects :: HNil, Base]](
      api.getAll[Labels :: Projects :: HNil]
    )
    typed[MultipleReadResourceDefinition[F, P, Labels :: Projects :: HNil, Base]](
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
