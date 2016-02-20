package ru.pavkin.todoist.api.circe.decoders

import io.circe._
import io.circe.generic.auto._
import ru.pavkin.todoist.api.core.dto._

trait DTODecoders extends PlainCoproductDecoder {

  implicit val projectDTODecoder = Decoder[Project]
  implicit val labelDTODecoder = Decoder[Label]

  implicit val projectsDTODecoder = Decoder[List[Project]]
  implicit val labelsDTODecoder = Decoder[List[Label]]

  implicit val allResourcesDecoder = Decoder[AllResources]

  implicit val commandErrorDecoder = Decoder[CommandError]
  implicit val commandStatusDecoder = Decoder[CommandStatus]
  implicit val commandResultDecoder = Decoder[CommandResult]
}
