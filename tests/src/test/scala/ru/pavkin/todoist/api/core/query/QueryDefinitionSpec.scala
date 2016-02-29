package ru.pavkin.todoist.api.core.query

import cats.{FlatMap, Id}
import ru.pavkin.todoist.api.core.decoder.SingleResponseDecoder
import ru.pavkin.todoist.api.core.{HasRawRequest, RequestDefinitionSpec}
import ru.pavkin.todoist.api.utils.Flattener
import shapeless.HNil
import shapeless.test.{illTyped, typed}

import scala.util.{Failure, Success, Try}

class QueryDefinitionSpec extends RequestDefinitionSpec {

  implicit val tryFlatMap: FlatMap[Try] = new FlatMap[Try] {
    def flatMap[A, B](fa: Try[A])(f: (A) => Try[B]): Try[B] = fa.flatMap(f)
    def map[A, B](fa: Try[A])(f: (A) => B): Try[B] = fa.map(f)
  }

  val flattener = new Flattener[Option, Id, Try] {
    def flatten[T](o: Id[Try[T]]): Option[T] = o.toOption
  }

  def parseNonNegative(i: Int): Try[String] =
    if (i > 0) Success("positive")
    else if (i == 0) Success("zero")
    else Failure(new Exception)

  val nonNegativeStringParser = new SingleResponseDecoder[Try, Int, String] {
    def parse(resource: Int): Try[String] = parseNonNegative(resource)
  }

  implicit val toIntParser = new SingleResponseDecoder[Try, String, Int] {
    def parse(resource: String): Try[Int] = Try(resource.toInt)
  }

  implicit val toDoubleParser = new SingleResponseDecoder[Try, String, Double] {
    def parse(resource: String): Try[Double] = Try(resource.toDouble)
  }

  test("QueryDefinition returns the result of the parser") {
    check((i: Int) => {
      implicit val b: HasRawRequest[String] = HasRawRequest[String](Map("a" -> List(i.toString)))

      val r = new SingleQueryRequestDefinition[Option, Id, Try, String, String, Int](
        requestFactory,
        toIntRequestExecutor,
        flattener,
        nonNegativeStringParser
      )
      r.execute == parseNonNegative(i).toOption
    })
  }

  test("QueryDefinition combines") {
    check((s: Double) => {
      implicit val b: HasRawRequest[Int] = HasRawRequest[Int](Map("a" -> List(s.toString)))
      implicit val dd: HasRawRequest[Double] = HasRawRequest[Double](Map("a" -> List.empty))

      val r = new SingleQueryRequestDefinition[Option, Id, Try, Int, String, String](
        requestFactory,
        identityRequestExecutor[String],
        flattener,
        toIntParser
      )

      val combined = r.and[Double]

      illTyped("""r.and[String]""")
      illTyped("""r.and[Int]""")
      illTyped("""r.and[Long].and[Int]""")

      combined.execute == (for {
        i <- Try(s.toString.toInt).toOption
        d <- Try(s.toString.toDouble).toOption
      } yield d :: i :: HNil)
    })
  }
}
