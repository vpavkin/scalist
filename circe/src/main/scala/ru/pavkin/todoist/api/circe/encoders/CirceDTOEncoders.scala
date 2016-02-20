package ru.pavkin.todoist.api.circe.encoders

import io.circe.Encoder
import ru.pavkin.todoist.api.core.dto._
import io.circe.generic.auto._

trait CirceDTOEncoders {

  // commands
  implicit val addTaskEncoder = Encoder[AddTask]
  implicit val addProjecrEncoder = Encoder[AddProject]

  implicit def rawCommandEncoder1[A](implicit E: Encoder[A]) =
    Encoder[RawCommand[A]]

  implicit def rawCommandEncoder2[A](implicit E: Encoder[A]) =
    Encoder[RawCommandWithTempId[A]]

}
