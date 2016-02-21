package ru.pavkin.todoist.api.suite

import cats.Monad
import ru.pavkin.todoist.api.core.decoder.{SingleCommandResponseDecoder, SingleResponseDecoder}
import ru.pavkin.todoist.api.core.{CommandReturns, dto}
import ru.pavkin.todoist.api.core.dto._
import shapeless.{Inl, Inr}

trait DTOAPISuite[F[_], P[_], Base]
  extends AbstractDTOQueryAPISuite[F, P, Base, AllResources]
    with AbstractDTOCommandAPISuite[F, P, Base, RawCommandResult] {

  type Projects = List[Project]
  type Labels = List[Label]

  type CommandResult = dto.CommandResult
  type TempIdCommandResult = dto.TempIdCommandResult

  implicit def dtoToProjects(implicit M: Monad[P]): SingleResponseDecoder.Aux[P, AllResources, Projects] =
    fromResourceDtoDecoder(_.Projects)("projects")

  implicit def dtoToLabels(implicit M: Monad[P]): SingleResponseDecoder.Aux[P, AllResources, Labels] =
    fromResourceDtoDecoder(_.Labels)("labels")

  implicit def dtoToRawCommandResult[A]
  (implicit M: Monad[P]): SingleCommandResponseDecoder.Aux[P, RawCommand[A], RawCommandResult, CommandResult] =
    fromCommandResultDtoDecoder[RawCommand[A], CommandResult] {
      (command, result) => result.SyncStatus.get(command.uuid.toString).map(CommandResult)
    }

  implicit def dtoToRawTempIdCommandResult[A](implicit M: Monad[P])
  : SingleCommandResponseDecoder.Aux[P, RawTempIdCommand[A], RawCommandResult, TempIdCommandResult] =
    fromCommandResultDtoDecoder[RawTempIdCommand[A], TempIdCommandResult]((command, result) =>
      result.SyncStatus.get(command.uuid.toString).flatMap {
        case Inr(Inl(error)) => Some(TempIdFailure(error))
        case other => result.TempIdMapping.flatMap(_.get(command.temp_id.toString)).map(TempIdSuccess(other, _))
      })

  implicit def rawCommandReturns1[A]: CommandReturns.Aux[RawCommand[A], CommandResult] =
    new CommandReturns[RawCommand[A]] {
      type Result = CommandResult
    }

  implicit def rawCommandReturns2[A]: CommandReturns.Aux[RawTempIdCommand[A], TempIdCommandResult] =
    new CommandReturns[RawTempIdCommand[A]] {
      type Result = TempIdCommandResult
    }
}
