package ru.pavkin.todoist.api.core

import ru.pavkin.todoist.api.Token

trait UnauthorizedAPI[F[_], P[_], Base] {

  def withToken(token: Token): API[F, P, Base]

}
