package ru.pavkin.todoist.api.dispatch.core

import dispatch.{Req, url}
import ru.pavkin.todoist.api
import ru.pavkin.todoist.api.Token
import ru.pavkin.todoist.api.core.AuthorizedRequestFactory

case class DispatchAuthorizedRequestFactory(token: Token) extends AuthorizedRequestFactory[Vector[String], Req] {

  def produce(resources: Vector[String]): Req =
    url(api.url)
      .POST
      .<<?(Map(
        "token" -> token,
        "seq_no" -> "0",
        "resource_types" -> s"[${resources.map("\"" + _ + "\"").mkString(",")}]"
      ))
}

