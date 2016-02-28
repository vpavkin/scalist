package ru.pavkin.todoist.api.core

import shapeless.tag
import shapeless.tag._

object tags {
  trait ProjectId
  trait LabelId
  trait TaskId
  trait UserId
  trait NoteId
  trait FilterId

  trait Projects
  trait Labels

  trait Syntax {
    implicit class ResourceIdTagOps[A: IsResourceId](a: A) {
      def projectId: A @@ tags.ProjectId = tag[tags.ProjectId](a)
      def labelId: A @@ tags.LabelId = tag[tags.LabelId](a)
      def taskId: A @@ tags.TaskId = tag[tags.TaskId](a)
      def userId: A @@ tags.UserId = tag[tags.UserId](a)
      def noteId: A @@ tags.NoteId = tag[tags.NoteId](a)
      def filterId: A @@ tags.FilterId = tag[tags.FilterId](a)
    }
  }

  object syntax extends Syntax
}
