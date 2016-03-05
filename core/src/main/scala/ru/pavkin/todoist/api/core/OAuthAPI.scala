package ru.pavkin.todoist.api.core

import ru.pavkin.todoist.api._
import ru.pavkin.todoist.api.core.decoder.SingleResponseDecoder
import ru.pavkin.todoist.api.core.model.{TokenExchange, TokenScope}

/**
  * Supplies helper methods for Todoist authorization API
  */
trait OAuthAPI[F[_], P[_], Base] {

  /**
    * Returns an oAuth step 3 request definition, that upon execution
    * exchanges security code on API token.
    *
    * @param request Exchange request parameters
    */
  def oAuthStep3(request: TokenExchange)
                (implicit
                 trr: ToRawRequest[TokenExchange],
                 parser: SingleResponseDecoder[P, Base, model.AccessToken])
  : RequestDefinition[F, P, model.AccessToken, Base]

  /**
    * Constructs a url for oAuth step 1 request, based on supplied parameters
    *
    * @param clientId client id
    * @param scopes   a set of scopes that are required by authorizing application
    * @param state    unique state parameter
    */
  def oAuthStep1URL(clientId: String, scopes: Set[TokenScope], state: String): String =
    s"$oAuthURL?" + List(
      "client_id" -> clientId,
      "state" -> state,
      "scope" -> scopes.map(_.name).mkString(",")
    ).map(p => s"${p._1}=${p._2}").mkString("&")
}
