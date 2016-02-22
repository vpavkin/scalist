package ru.pavkin.todoist.api.circe.decoders

import io.circe._
import io.circe.generic.auto._
import ru.pavkin.todoist.api.core.dto._

trait CirceDTODecoders extends PlainCoproductDecoder {

  // resources
  implicit val projectDTODecoder = Decoder[Project]
  implicit val labelDTODecoder = Decoder[Label]

  implicit val projectsDTODecoder = Decoder[List[Project]]
  implicit val labelsDTODecoder = Decoder[List[Label]]

  implicit val allResourcesDecoder = Decoder[AllResources]

  // commands
  implicit val commandErrorDecoder = Decoder[RawCommandError]
  implicit val commandStatusDecoder = Decoder[RawCommandStatus]
  implicit val commandResultDecoder = Decoder[RawCommandResult]
}
