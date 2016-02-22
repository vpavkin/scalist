package ru.pavkin.todoist.api.core.model

import java.util.Date

import ru.pavkin.todoist.api.core.tags
import shapeless.tag.@@

case class Task(id: Int @@ tags.TaskId,
                userId: Int @@ tags.UserId,
                projectId: Int @@ tags.ProjectId,
                content: String,
                date: Option[TaskDate],
                priority: Priority,
                indent: Indent,
                order: Int,
                dayOrder: Int,
                isCollapsed: Boolean,
                labels: List[Int @@ tags.LabelId],
                assignedBy: Option[Int @@ tags.UserId],
                responsible: Option[Int @@ tags.UserId],
                isCompleted: Boolean,
                isInHistory: Boolean,
                isDeleted: Boolean,
                isArchived: Boolean,
                addedAt: Date)
