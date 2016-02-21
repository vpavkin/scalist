package ru.pavkin.todoist.api.core.model

import java.util.UUID

sealed trait TodoistCommandResult {
  val uuid: UUID
  def isSuccess: Boolean
}
case class CommandResult(uuid: UUID, status: CommandStatus) extends TodoistCommandResult {
  def isSuccess = status.isSuccess
}
case class TempIdCommandResult(uuid: UUID, status: TempIdCommandStatus) extends TodoistCommandResult {
  def isSuccess = status.isSuccess
}

sealed trait CommandStatus {
  def isSuccess: Boolean
}
sealed trait SingleCommandStatus extends CommandStatus
case class CommandFailure(code: Int, message: String) extends SingleCommandStatus {
  def isSuccess: Boolean = false
}
case object CommandSuccess extends SingleCommandStatus {
  def isSuccess: Boolean = true
}
case class MultiItemCommandStatus(items: Map[Int, SingleCommandStatus]) extends CommandStatus {
  def isSuccess = items.values.forall(_.isSuccess)
}

sealed trait TempIdCommandStatus {
  def isSuccess: Boolean
}
case class TempIdFailure(code: Int, message: String) extends TempIdCommandStatus {
  def isSuccess = false
}
case class TempIdSuccess(tempId: UUID, realId: Int) extends TempIdCommandStatus {
  def isSuccess = true
}
