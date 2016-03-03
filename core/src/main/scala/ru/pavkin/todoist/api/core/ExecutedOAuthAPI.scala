package ru.pavkin.todoist.api.core

import ru.pavkin.todoist.api.core.decoder.SingleResponseDecoder
import ru.pavkin.todoist.api.core.model.{AccessToken, TokenExchange}
import ru.pavkin.todoist.api.core.query.SingleQueryRequestDefinition

trait ExecutedOAuthAPI[F[_], L[_], P[_], Req, Base]
  extends OAuthAPI[F, P, Base]
    with ExecutedAPI[F, L, P, Req, Base] {

  def oAuthStep3(request: TokenExchange)
                (implicit trr: ToRawRequest[TokenExchange],
                 parser: SingleResponseDecoder[P, Base, AccessToken]): RequestDefinition[F, P, AccessToken, Base] = {

    implicit val hrr: HasRawRequest[AccessToken] = HasRawRequest(trr.rawRequest(request))

    new SingleQueryRequestDefinition[F, L, P, model.AccessToken, Req, Base](
      requestFactory, executor, flattener, parser
    )
  }
}
