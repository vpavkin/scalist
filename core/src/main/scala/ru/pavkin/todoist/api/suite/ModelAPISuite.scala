package ru.pavkin.todoist.api.suite

import cats.Monad
import cats.std.list._
import ru.pavkin.todoist.api.RawRequest
import ru.pavkin.todoist.api.core.FromDTO.syntax._
import ru.pavkin.todoist.api.core._
import ru.pavkin.todoist.api.core.decoder.{SingleCommandResponseDecoder, SingleResponseDecoder}
import ru.pavkin.todoist.api.core.dto._
import ru.pavkin.todoist.api.core.model.{SimpleCommand, TempIdCommand}
import shapeless.{Inl, Inr}

trait ModelAPISuite[F[_], P[_], Base]
  extends AbstractDTOQueryAPISuite[F, P, Base, AllResources]
    with AbstractDTOCommandAPISuite[F, P, Base, RawCommandResult] {

  type Projects = List[model.Project]
  type Labels = List[model.Label]

  type CommandResult = dto.CommandResult
  type TempIdCommandResult = dto.TempIdCommandResult

  implicit def dtoToProjects(implicit M: Monad[P]): SingleResponseDecoder.Aux[P, AllResources, Projects] =
    fromResourceDtoDecoder(_.Projects.map(_.toModel))("projects")

  implicit def dtoToLabels(implicit M: Monad[P]): SingleResponseDecoder.Aux[P, AllResources, Labels] =
    fromResourceDtoDecoder(_.Labels.map(_.toModel))("labels")

  implicit def dtoToRawCommandResult[A <: SimpleCommand]
  (implicit M: Monad[P]): SingleCommandResponseDecoder.Aux[P, A, RawCommandResult, CommandResult] =
    fromCommandResultDtoDecoder[A, CommandResult] {
      (command, result) => result.SyncStatus.get(command.uuid.toString).map(CommandResult)
    }

  implicit def dtoToRawTempIdCommandResult[A <: TempIdCommand](implicit M: Monad[P])
  : SingleCommandResponseDecoder.Aux[P, A, RawCommandResult, TempIdCommandResult] =
    fromCommandResultDtoDecoder[A, TempIdCommandResult]((command, result) =>
      result.SyncStatus.get(command.uuid.toString).flatMap {
        case Inr(Inl(error)) => Some(TempIdFailure(error))
        case other => result.TempIdMapping.flatMap(_.get(command.tempId.toString)).map(TempIdSuccess(other, _))
      })

  implicit def commandReturns[T <: SimpleCommand]: CommandReturns.Aux[T, CommandResult] =
    new CommandReturns[T] {
      type Result = CommandResult
    }

  implicit def tempIdCommandReturns[T <: TempIdCommand]: CommandReturns.Aux[T, TempIdCommandResult] =
    new CommandReturns[T] {
      type Result = TempIdCommandResult
    }

  import ToRawRequest.syntax._

  implicit def commandToRawReq[A <: SimpleCommand, B]
  (implicit
   T: HasCommandType[A],
   D: ToDTO[A, B],
   T2: ToRawRequest[RawCommand[B]]): ToRawRequest[A] = new ToRawRequest[A] {
    def rawRequest(c: A): RawRequest =
      RawCommand(T.commandType, c.uuid, D.produce(c)).toRawRequest
  }

  implicit def tempIdCommandToRawReq[A <: TempIdCommand, B]
  (implicit
   T: HasCommandType[A],
   D: ToDTO[A, B],
   T2: ToRawRequest[RawTempIdCommand[B]]): ToRawRequest[A] = new ToRawRequest[A] {
    def rawRequest(c: A): RawRequest =
      RawTempIdCommand(T.commandType, c.uuid, D.produce(c), c.tempId).toRawRequest
  }

}
