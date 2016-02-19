package ru.pavkin.todoist.api.circe

import io.circe.{Json, Decoder}
import ru.pavkin.todoist.api.suite.APISuite

// todo: extract labels
trait CirceAPISuite[F[_]] extends APISuite[F, CirceDecoder.Result, Json] {

  def projectsDecoder(implicit D: Decoder[Projects]): CirceDecoder[Projects] =
    CirceDecoder[Projects](_.asObject.flatMap(_ ("Projects")))

  def labelsDecoder(implicit D: Decoder[Labels]): CirceDecoder[Labels] =
    CirceDecoder[Labels](_.asObject.flatMap(_ ("Labels")))

}
