package ru.pavkin.todoist.api.dispatch.core

import dispatch.{Req, url}
import ru.pavkin.todoist.api
import ru.pavkin.todoist.api.{RawRequest, Token}
import ru.pavkin.todoist.api.core.AuthorizedRequestFactory

case class DispatchAuthorizedRequestFactory(token: Token) extends AuthorizedRequestFactory[RawRequest, Req] {

  def produce(resources: RawRequest): Req =
    url(api.syncURL)
      .POST
      .<<?(Map(
        "token" -> token,
        "seq_no" -> "0"
      ) ++ resources.mapValues(list => s"[${list.reverse.mkString(",")}]"
      ))
}

