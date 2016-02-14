package ru.pavkin.todoist.api.core

import ru.pavkin.todoist.api.Token

trait UnauthorizedAPI[F[_]] {

  def authorize(token: Token): API[F]

}
