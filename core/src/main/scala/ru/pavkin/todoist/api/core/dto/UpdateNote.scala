package ru.pavkin.todoist.api.core.dto

import ru.pavkin.todoist.api.core.IsResourceId

case class UpdateNote[T: IsResourceId](id: T,
                                       content: Option[String] = None)
