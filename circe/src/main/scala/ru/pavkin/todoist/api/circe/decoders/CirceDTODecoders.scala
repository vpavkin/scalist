package ru.pavkin.todoist.api.circe.decoders

import cats.data.Xor
import io.circe._
import io.circe.generic.auto._
import ru.pavkin.todoist.api.core.dto._

trait CirceDTODecoders extends PlainCoproductDecoder {

  // resources
  implicit val projectDTODecoder = Decoder[Project]
  implicit val labelDTODecoder = Decoder[Label]
  implicit val taskDTODecoder = Decoder[Task]
  implicit val fileDTODecoder = Decoder[FileAttachment]
  implicit val noteDTODecoder = Decoder[Note]
  implicit val filterDTODecoder = Decoder[Filter]
  implicit val reminderDTODecoder = Decoder[Reminder]
  implicit val tzOffsetDTODecoder = Decoder.instance(c =>
    Xor.fromOption(c.focus.asArray.flatMap {
      case List(gmtStr, hr, min, _) =>
        for {
          gmtString <- gmtStr.asString
          hour <- hr.asNumber.flatMap(_.toInt)
          minute <- min.asNumber.flatMap(_.toInt)
        } yield TimeZoneOffset(gmtString, hour, minute)
      case _ =>
        None
    }, DecodingFailure("Couldn't parse timezone offset", Nil))
  )
  implicit val userDTODecoder = Decoder[User]

  implicit val projectsDTODecoder = Decoder[List[Project]]
  implicit val labelsDTODecoder = Decoder[List[Label]]
  implicit val tasksDTODecoder = Decoder[List[Task]]
  implicit val notesDTODecoder = Decoder[List[Note]]
  implicit val filtersDTODecoder = Decoder[List[Filter]]
  implicit val remindersDTODecoder = Decoder[List[Reminder]]

  implicit val allResourcesDecoder = Decoder[AllResources]

  // commands
  implicit val commandErrorDecoder = Decoder[RawCommandError]
  implicit val commandStatusDecoder = Decoder[RawCommandStatus]
  implicit val commandResultDecoder = Decoder[RawCommandResult]
}
