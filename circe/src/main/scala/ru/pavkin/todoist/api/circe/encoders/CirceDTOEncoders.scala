package ru.pavkin.todoist.api.circe.encoders

import io.circe.Encoder
import ru.pavkin.todoist.api.core.IsResourceId
import ru.pavkin.todoist.api.core.dto._
import io.circe.generic.auto._

trait CirceDTOEncoders {

  // commands
  implicit def addTaskEncoder[A: IsResourceId](implicit E: Encoder[A]) = Encoder[AddTask[A]]
  implicit val addTaskToInboxEncoder = Encoder[AddTaskToInbox]
  implicit val addProjectEncoder = Encoder[AddProject]
  implicit val addLabelEncoder = Encoder[AddLabel]
  implicit def updateTaskEncoder[A: IsResourceId](implicit E: Encoder[A]) = Encoder[UpdateTask[A]]
  implicit def updateProjectEncoder[A: IsResourceId](implicit E: Encoder[A]) = Encoder[UpdateProject[A]]
  implicit def updateLabelEncoder[A: IsResourceId](implicit E: Encoder[A]) = Encoder[UpdateLabel[A]]

  implicit def mulltiIdEncoder[A: IsResourceId](implicit E: Encoder[A]) = Encoder[MultipleIdCommand[A]]

  implicit def rawCommandEncoder[A](implicit E: Encoder[A]) =
    Encoder[RawCommand[A]]

  implicit def rawTempIdCommandEncoder[A](implicit E: Encoder[A]) =
    Encoder[RawTempIdCommand[A]]

}
