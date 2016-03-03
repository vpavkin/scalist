package ru.pavkin.todoist.api.core

import ru.pavkin.todoist.api._
import ru.pavkin.todoist.api.core.decoder.SingleResponseDecoder
import ru.pavkin.todoist.api.core.model.{TokenExchange, TokenScope}

trait OAuthAPI[F[_], P[_], Base] {

  def oAuthStep3(request: TokenExchange)
                (implicit
                 trr: ToRawRequest[TokenExchange],
                 parser: SingleResponseDecoder[P, Base, model.AccessToken])
  : RequestDefinition[F, P, model.AccessToken, Base]


  def oAuthStep1URL(clientId: String, scopes: Set[TokenScope], state: String): String =
    s"$oAuthURL?" + List(
      "client_id" -> clientId,
      "state" -> state,
      "scope" -> scopes.map(_.name).mkString(",")
    ).map(p => s"${p._1}=${p._2}").mkString("&")
}
