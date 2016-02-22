package ru.pavkin.todoist.api.suite

import ru.pavkin.todoist.api.core.tags

trait CommandAPISuite extends tags.Syntax {
  type CommandResult
  type TempIdCommandResult
}
