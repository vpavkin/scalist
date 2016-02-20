package ru.pavkin.todoist.api.core

import shapeless.tag.@@

package object model {
  type ProjectId = Int @@ tags.ProjectId
  type LabelId = Int @@ tags.LabelId
  type UserId = Int @@ tags.UserId
  type TaskId = Int @@ tags.UserId

  type Item = Task
}
