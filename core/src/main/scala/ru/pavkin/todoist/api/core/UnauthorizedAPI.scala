package ru.pavkin.todoist.api.core

import ru.pavkin.todoist.api.Token

/**
  * Unauthorized API provides two methods: `withToken(token: String)` that returns Authorized API
  * and `auth`, that returns a wrapper for OAuth related helpers
  */
trait UnauthorizedAPI[F[_], P[_], Base] {

  /**
    * Creates an authorized API Client that has access to most of the API calls like
    * querying resources and performing commands
    *
    * @param token User API token
    * @return Authorized API Client
    */
  def withToken(token: Token): AuthorizedAPI[F, P, Base]

  /**
    * Returns a client for OAuth related helpers
    */
  def auth: OAuthAPI[F, P, Base]

}
