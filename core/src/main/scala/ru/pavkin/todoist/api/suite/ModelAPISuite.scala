package ru.pavkin.todoist.api.suite

import cats.Monad
import cats.std.list._
import ru.pavkin.todoist.api.RawRequest
import ru.pavkin.todoist.api.core.FromDTO.syntax._
import ru.pavkin.todoist.api.core.decoder.{SingleCommandResponseDecoder, SingleResponseDecoder}
import ru.pavkin.todoist.api.core.dto
import ru.pavkin.todoist.api.core.model._
import ru.pavkin.todoist.api.core.model.util.{CombineCommands, CommandResultHList, ReversedAtSyntax}
import ru.pavkin.todoist.api.core._

trait ModelAPISuite[F[_], P[_], Base]
  extends AbstractDTOQueryAPISuite[F, P, Base, dto.AllResources]
    with AbstractDTOCommandAPISuite[F, P, Base, dto.RawCommandResult]
    with AbstractOAuthAPISuite[F, P, Base, dto.AccessToken] {

  type Projects = List[model.Project]
  type Labels = List[model.Label]
  type Tasks = List[model.Task]
  type Notes = List[model.Note]
  type Filters = List[model.Filter]
  type Reminders = List[model.Reminder]
  type User = model.User

  type CommandResult = model.CommandResult
  type TempIdCommandResult = model.TempIdCommandResult

  implicit def dtoToProjects(implicit M: Monad[P]): SingleResponseDecoder[P, dto.AllResources, Projects] =
    fromResourceDtoDecoder(_.Projects.map(_.toModel))("projects")

  implicit def dtoToLabels(implicit M: Monad[P]): SingleResponseDecoder[P, dto.AllResources, Labels] =
    fromResourceDtoDecoder(_.Labels.map(_.toModel))("labels")

  implicit def dtoToTasks(implicit M: Monad[P]): SingleResponseDecoder[P, dto.AllResources, Tasks] =
    fromResourceDtoDecoder(_.Items.map(_.toModel))("tasks")

  implicit def dtoToNotes(implicit M: Monad[P]): SingleResponseDecoder[P, dto.AllResources, Notes] =
    fromResourceDtoDecoder(_.Notes.map(_.toModel))("notes")

  implicit def dtoToFilters(implicit M: Monad[P]): SingleResponseDecoder[P, dto.AllResources, Filters] =
    fromResourceDtoDecoder(_.Filters.map(_.toModel))("filters")

  implicit def dtoToReminders(implicit M: Monad[P]): SingleResponseDecoder[P, dto.AllResources, Reminders] =
    fromResourceDtoDecoder(_.Reminders.map(_.toModel))("reminders")

  implicit def dtoToUser(implicit M: Monad[P]): SingleResponseDecoder[P, dto.AllResources, User] =
    fromResourceDtoDecoder(_.User.map(_.toModel))("user")

  implicit def dtoToRawCommandResult[A <: SimpleCommand]
  (implicit M: Monad[P]): SingleCommandResponseDecoder.Aux[P, A, dto.RawCommandResult, CommandResult] =
    fromCommandResultDtoDecoder[A, CommandResult] { (command, result) =>
      result.SyncStatus.get(command.uuid.toString).map(status =>
        CommandResult(command.uuid, status.toModel)
      )
    }

  implicit def dtoToRawTempIdCommandResult[A <: TempIdCommand[_]](implicit M: Monad[P])
  : SingleCommandResponseDecoder.Aux[P, A, dto.RawCommandResult, TempIdCommandResult] =
    fromCommandResultDtoDecoder[A, TempIdCommandResult](FromDTO.tempIdCommandStatusFromDTO(_, _))

  implicit def commandReturns[T <: SimpleCommand]: CommandReturns.Aux[T, CommandResult] =
    new CommandReturns[T] {
      type Result = CommandResult
    }

  implicit def tempIdCommandReturns[T <: TempIdCommand[_]]: CommandReturns.Aux[T, TempIdCommandResult] =
    new CommandReturns[T] {
      type Result = TempIdCommandResult
    }

  import ToRawRequest.syntax._

  implicit def commandToRawReq[A <: SimpleCommand, B]
  (implicit
   T: HasCommandType[A],
   D: ToDTO[A, B],
   T2: ToRawRequest[dto.RawCommand[B]]): ToRawRequest[A] = new ToRawRequest[A] {
    def rawRequest(c: A): RawRequest =
      dto.RawCommand(T.commandType, c.uuid, D.produce(c)).toRawRequest
  }

  implicit def tempIdCommandToRawReq[A <: TempIdCommand[_], B]
  (implicit
   T: HasCommandType[A],
   D: ToDTO[A, B],
   T2: ToRawRequest[dto.RawTempIdCommand[B]]): ToRawRequest[A] = new ToRawRequest[A] {
    def rawRequest(c: A): RawRequest =
      dto.RawTempIdCommand(T.commandType, c.uuid, D.produce(c), c.tempId).toRawRequest
  }

  def dtoToAccessToken(implicit M: Monad[P]): SingleResponseDecoder[P, dto.AccessToken, model.AccessToken] =
    SingleResponseDecoder.using(d => M.pure(d.toModel))

  /**
    * Syntax helpers for this API suite
    *
    * @see [[ru.pavkin.todoist.api.suite.QueryAPISuite.QuerySyntax.HListQueryOps.projects]]
    * @see [[ru.pavkin.todoist.api.core.model.util.ReversedAtSyntax.ReversedAtHListOps.resultFor]]
    * @see [[ru.pavkin.todoist.api.core.model.util.CommandResultHList.Syntax.CommandResultHListOps.resultFor]]
    * @see [[ru.pavkin.todoist.api.core.model.util.CommandResultHList.Syntax.CommandResultHListOps.isSuccess]]
    * @see [[ru.pavkin.todoist.api.core.model.util.CombineCommands.Syntax.CombineCommandsOps.:+]]
    * @see [[ru.pavkin.todoist.api.core.model.util.CombineCommands.Syntax.TempIdProduceCommandsOps.forIt]]
    * @see [[ru.pavkin.todoist.api.core.model.util.CombineCommands.Syntax.TempIdProduceCommandsOps.andForIt]]
    * @see [[ru.pavkin.todoist.api.core.model.util.CombineCommands.Syntax.TempIdProduceCommandsOps.andForItAll]]
    */
  object syntax
    extends QuerySyntax
      with ReversedAtSyntax
      with CommandResultHList.Syntax
      with CombineCommands.Syntax

}
