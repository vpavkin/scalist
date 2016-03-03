package ru.pavkin.todoist.api.suite

import cats.Monad
import ru.pavkin.todoist.api.core.{model, ToRawRequest}
import ru.pavkin.todoist.api.core.decoder.SingleResponseDecoder
import ru.pavkin.todoist.api.core.model.TokenExchange

trait AbstractOAuthAPISuite[F[_], P[_], Base, TokenDTO] {

  def accessTokenDtoDecoder: SingleResponseDecoder[P, Base, TokenDTO]
  def dtoToAccessToken(implicit M: Monad[P]): SingleResponseDecoder[P, TokenDTO, model.AccessToken]

  implicit def accessTokenDecoder(implicit M: Monad[P]): SingleResponseDecoder[P, Base, model.AccessToken] =
    accessTokenDtoDecoder.compose(dtoToAccessToken)

  implicit val tokenExchangeToRawRequest: ToRawRequest[TokenExchange] =
    ToRawRequest(t => Map(
      "client_id" -> t.clientId,
      "client_secret" -> t.clientSecret,
      "code" -> t.authCode
    ).mapValues(List(_)))

}
