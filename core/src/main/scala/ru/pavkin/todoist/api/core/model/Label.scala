package ru.pavkin.todoist.api.core.model

case class Label(id: LabelId,
                 userId: UserId,
                 name: String,
                 color: LabelColor,
                 order: Int,
                 isDeleted: Int)
