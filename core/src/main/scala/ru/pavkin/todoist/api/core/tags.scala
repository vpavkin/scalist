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
  trait ReminderId

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
      def reminderId: A @@ tags.ReminderId = tag[tags.ReminderId](a)
    }

    implicit class ResourceIdListTagOps[A: IsResourceId](a: List[A]) {
      def projectIds: List[A @@ tags.ProjectId] = a.map(_.projectId)
      def labelIds: List[A @@ tags.LabelId] = a.map(_.labelId)
      def taskIds: List[A @@ tags.TaskId] = a.map(_.taskId)
      def userIds: List[A @@ tags.UserId] = a.map(_.userId)
      def noteIds: List[A @@ tags.NoteId] = a.map(_.noteId)
      def filterIds: List[A @@ tags.FilterId] = a.map(_.filterId)
      def reminderIds: List[A @@ tags.ReminderId] = a.map(_.reminderId)
    }
  }

  object syntax extends Syntax
}
