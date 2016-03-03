package ru.pavkin.todoist.api.core.dto

import ru.pavkin.todoist.api.core.IsResourceId

case class MultipleIdCommand[T: IsResourceId](ids: List[T])
