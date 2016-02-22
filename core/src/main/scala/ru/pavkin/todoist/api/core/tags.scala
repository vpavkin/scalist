package ru.pavkin.todoist.api.core

import ru.pavkin.todoist.api.core.dto.IsResourceId
import shapeless.tag
import shapeless.tag._

object tags {
  trait ProjectId
  trait LabelId
  trait TaskId
  trait UserId

  trait Projects
  trait Labels

  trait Syntax {
    implicit class ResourceIdTagOps[A: IsResourceId](a: A) {
      def projectId: A @@ tags.ProjectId = tag[tags.ProjectId](a)
      def labelId: A @@ tags.LabelId = tag[tags.LabelId](a)
      def taskId: A @@ tags.TaskId = tag[tags.TaskId](a)
      def userId: A @@ tags.UserId = tag[tags.UserId](a)
    }
  }

  object syntax extends Syntax
}
