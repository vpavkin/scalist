package ru.pavkin.todoist.api.core.dto

import ru.pavkin.todoist.api.core.IsResourceId

case class SingleIdCommand[T: IsResourceId](id: T)
