package ru.pavkin.todoist.api.core.command

import cats.{FlatMap, Id}
import ru.pavkin.todoist.api.core.decoder.{SingleCommandResponseDecoder, SingleResponseDecoder}
import ru.pavkin.todoist.api.core.{CommandReturns, ToRawRequest, HasRawRequest, RequestDefinitionSpec}
import ru.pavkin.todoist.api.utils.Flattener
import shapeless.HNil
import shapeless.test.illTyped

import scala.util.{Failure, Success, Try}

class CommandDefinitionSpec extends RequestDefinitionSpec {

  case class IntCommand(i: Int)
  case class DoubleCommand(d: Double)
  case class StringCommand(s: String)

  implicit val tryFlatMap: FlatMap[Try] = new FlatMap[Try] {
    def flatMap[A, B](fa: Try[A])(f: (A) => Try[B]): Try[B] = fa.flatMap(f)
    def map[A, B](fa: Try[A])(f: (A) => B): Try[B] = fa.map(f)
  }

  val flattener = new Flattener[Option, Id, Try] {
    def flatten[T](o: Id[Try[T]]): Option[T] = o.toOption
  }

  def parseNonNegative(command: IntCommand)(i: Int): Try[String] =
    if (i > 0) Success("positive " + command.i)
    else if (i == 0) Success("zero" + command.i)
    else Failure(new Exception)

  val nonNegativeStringParser = new SingleCommandResponseDecoder[Try, IntCommand, Int] {
    type Out = String
    def parse(c: IntCommand)(resource: Int): Try[String] = parseNonNegative(c)(resource)
  }

  implicit val toIntParser = new SingleCommandResponseDecoder[Try, StringCommand, String] {
    type Out = Int
    def parse(c: StringCommand)(resource: String): Try[Int] = Try(resource.toInt)
  }

  implicit val toDoubleParser = new SingleCommandResponseDecoder[Try, DoubleCommand, String] {
    type Out = Double
    def parse(c: DoubleCommand)(resource: String): Try[Double] = Try(resource.toDouble)
  }

  implicit val itr: ToRawRequest[IntCommand] =
    ToRawRequest[IntCommand]((c: IntCommand) => Map("a" -> List(c.i.toString)))
  implicit val dtr: ToRawRequest[DoubleCommand] =
    ToRawRequest[DoubleCommand]((c: DoubleCommand) => Map("a" -> List(c.d.toString)))
  implicit val str: ToRawRequest[StringCommand] =
    ToRawRequest[StringCommand]((c: StringCommand) => Map("a" -> List(c.s.toString)))

  implicit val cr1: CommandReturns.Aux[IntCommand, String] = new CommandReturns[IntCommand] {
    type Result = String
  }
  implicit val cr2: CommandReturns.Aux[StringCommand, Int] = new CommandReturns[StringCommand] {
    type Result = Int
  }
  implicit val cr3: CommandReturns.Aux[DoubleCommand, Double] = new CommandReturns[DoubleCommand] {
    type Result = Double
  }

  test("CommandDefinition returns the result of the parser") {
    check((i: Int) => {
      val r = new SingleCommandRequestDefinition[Option, Id, Try, IntCommand, String, String, Int](
        requestFactory,
        toIntRequestExecutor,
        flattener,
        nonNegativeStringParser
      )(IntCommand(i))
      r.execute == parseNonNegative(IntCommand(i))(i).toOption
    })
  }

  test("CommandDefinition combines") {
    check((s: String, d: Double) => {
      val r = new SingleCommandRequestDefinition[Option, Id, Try, StringCommand, Int, String, String](
        requestFactory,
        identityRequestExecutor[String],
        flattener,
        toIntParser
      )(StringCommand(s))

      val combined = r.and(DoubleCommand(d))

      illTyped("""r.and[String]""")

      combined.execute == (for {
        i <- Try(s.toString.toInt).toOption
        d <- Try(s.toString.toDouble).toOption
      } yield d :: i :: HNil)
    })
  }
}
