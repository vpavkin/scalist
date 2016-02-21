package ru.pavkin.todoist.api.core.model

import java.util.UUID

case class CommandResult(uuid: UUID, status: CommandStatus)
case class TempIdCommandResult(uuid: UUID, status: TempIdCommandStatus)

sealed trait CommandStatus
sealed trait SingleCommandStatus extends CommandStatus
case class CommandFailure(code: Int, message: String) extends SingleCommandStatus
case object CommandSuccess extends SingleCommandStatus
case class MultiItemCommandStatus(items: Map[Int, SingleCommandStatus]) extends CommandStatus

sealed trait TempIdCommandStatus
case class TempIdFailure(code: Int, message: String) extends TempIdCommandStatus
case class TempIdSuccess(tempId: UUID, realId: Int) extends TempIdCommandStatus
