package ru.pavkin.todoist.api.core

import shapeless.tag
import shapeless.tag._

object tags {
  /**
    * Tag for project ids
    */
  trait ProjectId
  /**
    * Tag for label ids
    */
  trait LabelId
  /**
    * Tag for task ids
    */
  trait TaskId
  /**
    * Tag for user ids
    */
  trait UserId
  /**
    * Tag for note ids
    */
  trait NoteId
  /**
    * Tag for filter ids
    */
  trait FilterId
  /**
    * Tag for reminder ids
    */
  trait ReminderId

  trait Syntax {
    implicit class ResourceIdTagOps[A: IsResourceId](a: A) {
      /**
        * Tags raw value with [[ru.pavkin.todoist.api.core.tags.ProjectId]] tag
        * so that it can be used with the modal classes
        */
      def projectId: A @@ tags.ProjectId = tag[tags.ProjectId](a)
      /**
        * Tags raw value with [[ru.pavkin.todoist.api.core.tags.LabelId]] tag
        * so that it can be used with the modal classes
        */
      def labelId: A @@ tags.LabelId = tag[tags.LabelId](a)
      /**
        * Tags raw value with [[ru.pavkin.todoist.api.core.tags.TaskId]] tag
        * so that it can be used with the modal classes
        */
      def taskId: A @@ tags.TaskId = tag[tags.TaskId](a)
      /**
        * Tags raw value with [[ru.pavkin.todoist.api.core.tags.UserId]] tag
        * so that it can be used with the modal classes
        */
      def userId: A @@ tags.UserId = tag[tags.UserId](a)
      /**
        * Tags raw value with [[ru.pavkin.todoist.api.core.tags.NoteId]] tag
        * so that it can be used with the modal classes
        */
      def noteId: A @@ tags.NoteId = tag[tags.NoteId](a)
      /**
        * Tags raw value with [[ru.pavkin.todoist.api.core.tags.FilterId]] tag
        * so that it can be used with the modal classes
        */
      def filterId: A @@ tags.FilterId = tag[tags.FilterId](a)
      /**
        * Tags raw value with [[ru.pavkin.todoist.api.core.tags.ReminderId]] tag
        * so that it can be used with the modal classes
        */
      def reminderId: A @@ tags.ReminderId = tag[tags.ReminderId](a)
    }

    implicit class ResourceIdListTagOps[A: IsResourceId](a: List[A]) {
      /**
        * Tags a list of raw values with [[ru.pavkin.todoist.api.core.tags.ProjectId]] tag
        * so that it can be used with the modal classes
        */
      def projectIds: List[A @@ tags.ProjectId] = a.map(_.projectId)
      /**
        * Tags a list of raw values with [[ru.pavkin.todoist.api.core.tags.LabelId]] tag
        * so that it can be used with the modal classes
        */
      def labelIds: List[A @@ tags.LabelId] = a.map(_.labelId)
      /**
        * Tags a list of raw values with [[ru.pavkin.todoist.api.core.tags.TaskId]] tag
        * so that it can be used with the modal classes
        */
      def taskIds: List[A @@ tags.TaskId] = a.map(_.taskId)
      /**
        * Tags a list of raw values with [[ru.pavkin.todoist.api.core.tags.UserId]] tag
        * so that it can be used with the modal classes
        */
      def userIds: List[A @@ tags.UserId] = a.map(_.userId)
      /**
        * Tags a list of raw values with [[ru.pavkin.todoist.api.core.tags.NoteId]] tag
        * so that it can be used with the modal classes
        */
      def noteIds: List[A @@ tags.NoteId] = a.map(_.noteId)
      /**
        * Tags a list of raw values with [[ru.pavkin.todoist.api.core.tags.FilterId]] tag
        * so that it can be used with the modal classes
        */
      def filterIds: List[A @@ tags.FilterId] = a.map(_.filterId)
      /**
        * Tags a list of raw values with [[ru.pavkin.todoist.api.core.tags.ReminderId]] tag
        * so that it can be used with the modal classes
        */
      def reminderIds: List[A @@ tags.ReminderId] = a.map(_.reminderId)
    }
  }

  object syntax extends Syntax
}
