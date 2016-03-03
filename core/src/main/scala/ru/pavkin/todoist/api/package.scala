package ru.pavkin.todoist

import algebra.Semigroup
import cats.std.map._
import cats.std.list._

package object api {

  val syncURL = "https://todoist.com/API/v6/sync"
  val oAuthURL = "https://todoist.com/oauth/authorize"
  val tokenExchangeURL = "https://todoist.com/oauth/authorize"

  type Token = String
  type RawRequest = Map[String, List[String]]

  implicit val rawRequestSemigroup = Semigroup[RawRequest]

  def unexpected[T]: T = sys.error("Unexpected call")
}
