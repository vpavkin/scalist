package ru.pavkin.todoist.api.suite

import cats.Monad
import cats.std.list._
import ru.pavkin.todoist.api
import ru.pavkin.todoist.api.RawRequest
import ru.pavkin.todoist.api.core.FromDTO.syntax._
import ru.pavkin.todoist.api.core.decoder.{SingleCommandResponseDecoder, SingleResponseDecoder}
import ru.pavkin.todoist.api.core.model._
import ru.pavkin.todoist.api.core.model.util.{CombineCommands, CommandResultHList, ReversedAtSyntax}
import ru.pavkin.todoist.api.core.{dto, _}
import shapeless.{Inl, Inr}

trait ModelAPISuite[F[_], P[_], Base]
  extends AbstractDTOQueryAPISuite[F, P, Base, dto.AllResources]
    with AbstractDTOCommandAPISuite[F, P, Base, dto.RawCommandResult] {

  type Projects = List[model.Project]
  type Labels = List[model.Label]

  type CommandResult = model.CommandResult
  type TempIdCommandResult = model.TempIdCommandResult

  implicit def dtoToProjects(implicit M: Monad[P]): SingleResponseDecoder.Aux[P, dto.AllResources, Projects] =
    fromResourceDtoDecoder(_.Projects.map(_.toModel))("projects")

  implicit def dtoToLabels(implicit M: Monad[P]): SingleResponseDecoder.Aux[P, dto.AllResources, Labels] =
    fromResourceDtoDecoder(_.Labels.map(_.toModel))("labels")

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

  object syntax
    extends QuerySyntax
      with ReversedAtSyntax
      with CommandResultHList.Syntax
      with CombineCommands.Syntax

}
