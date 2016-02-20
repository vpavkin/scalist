package ru.pavkin.todoist.api.circe.decoders

import ru.pavkin.todoist.api.core.dto.{AllResources, Label, Project}
import io.circe._, io.circe.generic.semiauto._

trait DTODecoders {
  implicit val projectDTODecoder = deriveDecoder[Project]
  implicit val labelDTODecoder = deriveDecoder[Label]

  implicit val projectsDTODecoder = Decoder[List[Project]]
  implicit val labelsDTODecoder = Decoder[List[Label]]

  implicit val allResourcesDecoder = deriveDecoder[AllResources]
}
