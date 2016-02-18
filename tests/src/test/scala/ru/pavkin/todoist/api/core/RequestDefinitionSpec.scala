package ru.pavkin.todoist.api.core

import cats.Id
import org.scalatest.FunSuite
import org.scalatest.prop.Checkers
import ru.pavkin.todoist.api.RawRequest
import ru.pavkin.todoist.api.utils.Produce

trait RequestDefinitionSpec extends FunSuite with Checkers {

  type Req = String
  type Base = Int

  val requestFactory: RawRequest Produce Req = Produce(_.mkString)
  val stringLenghtRequestExecutor: RequestExecutor.Aux[Req, Id, Base] = new RequestExecutor[Req, Id] {
    type Res = Base
    def execute(r: Req): Id[Res] = r.length
  }

  val toIntRequestExecutor: RequestExecutor.Aux[Req, Id, Base] = new RequestExecutor[Req, Id] {
    type Res = Base
    def execute(r: Req): Id[Res] = r.toInt
  }

  def identityRequestExecutor[T]: RequestExecutor.Aux[T, Id, T] = new RequestExecutor[T, Id] {
    type Res = T
    def execute(r: T): Id[T] = r
  }
}
