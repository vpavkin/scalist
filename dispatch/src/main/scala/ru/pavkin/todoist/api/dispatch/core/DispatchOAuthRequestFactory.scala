package ru.pavkin.todoist.api.dispatch.core

import dispatch.{Req, url}
import ru.pavkin.todoist.api
import ru.pavkin.todoist.api.RawRequest
import ru.pavkin.todoist.api.utils.Produce

class DispatchOAuthRequestFactory extends Produce[RawRequest, Req] {

  def produce(request: RawRequest): Req =
    url(api.tokenExchangeURL)
      .POST
      .<<?(request.mapValues(_.mkString))
}

