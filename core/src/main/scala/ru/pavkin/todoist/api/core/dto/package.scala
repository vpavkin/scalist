package ru.pavkin.todoist.api.core

import shapeless.{:+:, CNil}

package object dto {

  type Item = Task

  case class RawCommandError(error_code: Int, error: String)
  type TempIdMapping = Map[String, Int]
  type RawCommandStatus = String :+: RawCommandError :+: RawMultipleItemCommandStatus :+: CNil
  type RawItemStatus = String :+: RawCommandError :+: CNil
  type RawMultipleItemCommandStatus = Map[String, RawItemStatus]
  type RawRequestStatus = Map[String, RawCommandStatus]

  case class RawCommandResult(SyncStatus: RawRequestStatus, TempIdMapping: Option[TempIdMapping])

  // synthetic DTOs
  case class CommandResult(SyncStatus: RawCommandStatus)
  case class CommandResultWithTempId(SyncStatus: RawCommandStatus, TempIdMapping: Int)
}
