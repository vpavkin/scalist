package ru.pavkin.todoist.api.core.plain

import ru.pavkin.todoist.api._
import ru.pavkin.todoist.api.core.parser.SingleResourceParser
import shapeless.test.{illTyped, typed}
import cats.Id
import ru.pavkin.todoist.api.core.{HasRawRequest, RequestDefinitionSpec}

class PlainRequestDefinitionSpec extends RequestDefinitionSpec {

  implicit def dummyParser[T]: SingleResourceParser.Aux[Id, Base, T] =
    SingleResourceParser.using[Id, Base, T](_ => unexpected[T])

  test("PlainRequestDefinition returns same result as executor") {
    check((i: Int) => {
      implicit val b: HasRawRequest[Boolean] = HasRawRequest[Boolean](Vector(i.toString))

      val r = new PlainSingleRequestDefinition[Id, Boolean, String, Int](
        requestFactory, stringLenghtRequestExecutor
      )
      typed[r.Out](i)
      r.execute == i.toString.length
    })
  }

  test("PlainRequestDefinition combines") {
    check((i: Int, l: Long, d: Double) => {
      implicit val b: HasRawRequest[Boolean] = HasRawRequest[Boolean](Vector(i.toString))
      implicit val ll: HasRawRequest[Long] = HasRawRequest[Long](Vector(l.toString))
      implicit val dd: HasRawRequest[Double] = HasRawRequest[Double](Vector(d.toString))

      val r = new PlainSingleRequestDefinition[Id, Boolean, String, Int](
        requestFactory, stringLenghtRequestExecutor
      )

      illTyped("""r.and[String]""")
      illTyped("""r.and[Boolean]""")
      illTyped("""r.and[Long].and[Boolean]""")

      val combined = r.and[Long]
      val triple = r.and[Long].and[Double]
      combined.execute == (i.toString + l.toString).length &&
        triple.execute == (i.toString + l.toString + d.toString).length
    })
  }
}
