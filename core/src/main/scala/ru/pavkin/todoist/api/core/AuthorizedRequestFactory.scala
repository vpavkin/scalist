package ru.pavkin.todoist.api.core

import ru.pavkin.todoist.api._
import ru.pavkin.todoist.api.utils.Produce

trait AuthorizedRequestFactory[Def, Req] extends Produce[Def, Req] {
  def token: Token
}
