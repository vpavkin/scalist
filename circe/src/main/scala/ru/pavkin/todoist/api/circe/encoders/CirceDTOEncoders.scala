package ru.pavkin.todoist.api.circe.encoders

import io.circe.Encoder
import ru.pavkin.todoist.api.core.IsResourceId
import ru.pavkin.todoist.api.core.dto._
import io.circe.generic.auto._

trait CirceDTOEncoders {

  // commands
  implicit def addTaskEncoder[A: IsResourceId](implicit E: Encoder[A]) = Encoder[AddTask[A]]
  implicit def addNoteEncoder[A: IsResourceId](implicit E: Encoder[A]) = Encoder[AddNote[A]]
  implicit def addReminderEncoder[A: IsResourceId](implicit E: Encoder[A]) = Encoder[AddReminder[A]]
  implicit val addTaskToInboxEncoder = Encoder[AddTaskToInbox]
  implicit val addProjectEncoder = Encoder[AddProject]
  implicit val addLabelEncoder = Encoder[AddLabel]
  implicit val addFilterEncoder = Encoder[AddFilter]
  implicit def updateTaskEncoder[A: IsResourceId](implicit E: Encoder[A]) = Encoder[UpdateTask[A]]
  implicit def updateProjectEncoder[A: IsResourceId](implicit E: Encoder[A]) = Encoder[UpdateProject[A]]
  implicit def updateLabelEncoder[A: IsResourceId](implicit E: Encoder[A]) = Encoder[UpdateLabel[A]]
  implicit def updateFilterEncoder[A: IsResourceId](implicit E: Encoder[A]) = Encoder[UpdateFilter[A]]
  implicit def updateNoteEncoder[A: IsResourceId](implicit E: Encoder[A]) = Encoder[UpdateNote[A]]

  implicit def singleIdEncoder[A: IsResourceId](implicit E: Encoder[A]) = Encoder[SingleIdCommand[A]]
  implicit def multiIdEncoder[A: IsResourceId](implicit E: Encoder[A]) = Encoder[MultipleIdCommand[A]]

  implicit val moveTasksEncoder = Encoder[MoveTasks]

  implicit def rawCommandEncoder[A](implicit E: Encoder[A]) =
    Encoder[RawCommand[A]]

  implicit def rawTempIdCommandEncoder[A](implicit E: Encoder[A]) =
    Encoder[RawTempIdCommand[A]]

}
