package ru.pavkin.todoist.api.core

import shapeless.{:+:, CNil}

package object dto {

  type Item = Task

  type TempIdMapping = Map[String, Int]

  case class CommandError(error_code: Int, error: String)

  type CommandStatus = String :+: CommandError :+: MultipleItemCommandStatus :+: CNil
  type ItemStatus = String :+: CommandError :+: CNil
  type MultipleItemCommandStatus = Map[String, ItemStatus]
  type RequestStatus = Map[String, CommandStatus]

  case class CommandResult(SyncStatus: RequestStatus, TempIdMapping: Option[TempIdMapping])

  // synthetic DTOs
  case class SingleCommandResult(SyncStatus: CommandStatus)
  case class SingleCommandResultWithTempId(SyncStatus: CommandStatus, TempIdMapping: Int)
}
