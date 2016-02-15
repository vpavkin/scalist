package ru.pavkin.todoist.api.circe

import io.circe.Decoder
import ru.pavkin.todoist.api.core.APISuite

trait CirceAPISuite extends APISuite {

  implicit def projectsDecoder(implicit D: Decoder[Projects]): CirceDecoder[Projects] =
    CirceDecoder[Projects](_.asObject.flatMap(_ ("Projects")))

  implicit def labelsDecoder(implicit D: Decoder[Labels]): CirceDecoder[Labels] =
    CirceDecoder[Labels](_.asObject.flatMap(_ ("Labels")))

}
