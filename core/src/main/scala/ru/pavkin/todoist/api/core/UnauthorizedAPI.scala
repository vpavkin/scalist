package ru.pavkin.todoist.api.core

import ru.pavkin.todoist.api.Token

trait UnauthorizedAPI[F[_], P[_], Base] {

  def withToken(token: Token): AuthorizedAPI[F, P, Base]

  def auth: OAuthAPI[F, P, Base]

}
