package ru.pavkin.todoist.api.core

import cats.{Apply, Monad}
import org.scalatest.prop.Checkers
import org.scalatest.{FunSuite, Matchers}
import ru.pavkin.todoist.api.core.query.{MultipleQueryDefinition, SingleQueryDefinition}
import ru.pavkin.todoist.api.suite.{AbstractDTOCommandAPISuite, AbstractDTOQueryAPISuite, FutureBasedAPISuite}
import shapeless.test._
import shapeless.{::, HNil}

import scala.concurrent.ExecutionContext

abstract class FutureBasedAPISuiteSpec[F[_] : Apply, P[_] : Monad, Base, ResDTO, ComResDTO]
(apiName: String)(implicit ec: ExecutionContext)
  extends FunSuite
    with Checkers
    with Matchers
    with AbstractDTOQueryAPISuite[F, P, Base, ResDTO]
    with AbstractDTOCommandAPISuite[F, P, Base, ComResDTO]
    with FutureBasedAPISuite[F, P, Base] {

  test(s"$apiName test suite") {
    val api = todoist.withToken("token")
    typed[SingleQueryDefinition[F, P, Projects, Base]](
      api.get[Projects]
    )

    typed[MultipleQueryDefinition[F, P, Labels :: Projects :: HNil, Base]](
      api.get[Projects].and[Labels]
    )
    typed[MultipleQueryDefinition[F, P, Projects :: Labels :: HNil, Base]](
      api.getAll[All]
    )
    typed[MultipleQueryDefinition[F, P, Labels :: Projects :: HNil, Base]](
      api.getAll[Labels :: Projects :: HNil]
    )
    typed[MultipleQueryDefinition[F, P, Labels :: Projects :: HNil, Base]](
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
