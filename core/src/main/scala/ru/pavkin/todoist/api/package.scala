package ru.pavkin.todoist

package object api {

  val url = "https://todoist.com/API/v6/sync"

  type Token = String
  type RawRequest = Vector[String]

  def unexpected[T]: T = sys.error("Unexpected call")
}
