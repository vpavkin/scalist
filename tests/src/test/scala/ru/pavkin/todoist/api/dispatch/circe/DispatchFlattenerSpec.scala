package ru.pavkin.todoist.api.dispatch.circe

import cats.data.Xor._
import io.circe.ParsingFailure
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.prop.Checkers
import org.scalatest.{FunSuite, Matchers}
import ru.pavkin.todoist.api.dispatch.impl.circe.DispatchAPI.{HTTPError, DecodingError, DispatchFlattener}
import ru.pavkin.todoist.api.dispatch.impl.circe.DispatchJsonRequestExecutor

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class DispatchFlattenerSpec extends FunSuite with Matchers with ScalaFutures with Checkers {

  val flattener = new DispatchFlattener

  test("ModelFlattener flatten properties") {
    check((a: Int) =>
      flattener.flatten(Future(Right(Right(a)))).futureValue == Right(a)
    )

    check((a: String) => {
      val error = io.circe.DecodingFailure(a, Nil)
      flattener.flatten(Future(Right(Left(error)))).futureValue == Left(DecodingError(error))
    })

    check((c: Int, a: Option[String]) => {
      val error = DispatchJsonRequestExecutor.HTTPError(c, a)
      flattener.flatten(Future(Left(error))).futureValue == Left(HTTPError(c, a))
    })

    check((s: String, e: Throwable) => {
      val error = DispatchJsonRequestExecutor.ParsingError(ParsingFailure(s, e))
      flattener.flatten(Future(Left(error))).futureValue == Left(DecodingError(ParsingFailure(s, e)))
    })

  }
}

