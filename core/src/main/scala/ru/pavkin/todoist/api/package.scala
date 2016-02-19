package ru.pavkin.todoist

import algebra.Semigroup
import cats.std.map._
import cats.std.list._

package object api {

  val url = "https://todoist.com/API/v6/sync"

  type Token = String
  type RawRequest = Map[String, List[String]]

  implicit val semigroup = Semigroup[RawRequest]

  def unexpected[T]: T = sys.error("Unexpected call")
}
