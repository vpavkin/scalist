package ru.pavkin.todoist.api.circe.decoders

import ru.pavkin.todoist.api.core.dto.{Label, Project}
import io.circe._, io.circe.generic.semiauto._

trait DTODecoders {
  implicit val projectDTODecoder = deriveDecoder[Project]
  implicit val labelDTODecoder = deriveDecoder[Label]

  implicit val projectsDTODecoder = Decoder[Vector[Project]]
  implicit val labelsDTODecoder = Decoder[Vector[Label]]
}
