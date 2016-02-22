package ru.pavkin.todoist.api.core.model

import ru.pavkin.todoist.api.core.tags
import shapeless.tag.@@

case class Label(id: Int @@ tags.LabelId,
                 userId: Int @@ tags.UserId,
                 name: String,
                 color: LabelColor,
                 order: Int,
                 isDeleted: Boolean)
