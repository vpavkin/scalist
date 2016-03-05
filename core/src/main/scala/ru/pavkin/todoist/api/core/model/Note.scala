package ru.pavkin.todoist.api.core.model

import java.util.Date

import ru.pavkin.todoist.api
import ru.pavkin.todoist.api.core.tags
import shapeless.tag.@@

sealed trait UploadState {
  def name: Option[String] = this match {
    case Pending => Some("pending")
    case Completed => Some("completed")
    case External => None
  }
}
case object Pending extends UploadState
case object Completed extends UploadState
case object External extends UploadState

object UploadState {
  def unsafe(str: Option[String]): UploadState = str match {
    case Some("pending") => Pending
    case Some("completed") => Completed
    case None => External
    case _ => api.unexpected
  }
}

case class FileAttachment(name: String,
                          size: Long,
                          mimeType: String,
                          url: String,
                          uploadState: UploadState)

case class Note(id: Int @@ tags.NoteId,
                postedBy: Int @@ tags.UserId,
                task: Int @@ tags.TaskId,
                project: Int @@ tags.ProjectId,
                content: String,
                attachment: Option[FileAttachment],
                subscribers: List[Int @@ tags.UserId],
                isDeleted: Boolean,
                isArchived: Boolean,
                postedAt: Date)
