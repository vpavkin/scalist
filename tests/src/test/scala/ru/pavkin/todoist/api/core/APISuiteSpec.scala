package ru.pavkin.todoist.api.core

import cats._
import org.scalatest.FunSuite
import org.scalatest.prop.Checkers
import shapeless.test._
import shapeless.{::, HNil}

abstract class APISuiteSpec[F[_] : Functor : Apply, P[_] : FlatMap, Base](apiName: String)
  extends FunSuite
    with Checkers
    with APISuite[F, P, Base] {

  test(s"$apiName test suite") {
    val api = todoist.authorize("token")
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
