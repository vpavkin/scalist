package ru.pavkin.todoist.api.suite

import cats.Monad
import ru.pavkin.todoist.api.core.decoder.{SingleCommandResponseDecoder, SingleResponseDecoder}
import ru.pavkin.todoist.api.core.{CommandReturns, dto}
import ru.pavkin.todoist.api.core.dto._

trait DTOAPISuite[F[_], P[_], Base]
  extends AbstractDTOQueryAPISuite[F, P, Base, AllResources]
    with AbstractDTOCommandAPISuite[F, P, Base, CommandResult] {

  type Projects = List[Project]
  type Labels = List[Label]

  type AddProject = dto.AddProject
  type AddTask = dto.AddTask

  type SingleCommandResult = dto.SingleCommandResult
  type SingleCommandResultWithTempId = dto.SingleCommandResultWithTempId

  implicit def dtoToProjects(implicit M: Monad[P]): SingleResponseDecoder.Aux[P, AllResources, Projects] =
    fromResourceDtoDecoder(_.Projects)("projects")

  implicit def dtoToLabels(implicit M: Monad[P]): SingleResponseDecoder.Aux[P, AllResources, Labels] =
    fromResourceDtoDecoder(_.Labels)("labels")

  implicit def dtoToRawCommand1[A]
  (implicit M: Monad[P]): SingleCommandResponseDecoder.Aux[P, RawCommand[A], CommandResult, SingleCommandResult] =
    fromCommandResultDtoDecoder[RawCommand[A], SingleCommandResult] {
      (command, result) => result.SyncStatus.get(command.uuid.toString).map(SingleCommandResult)
    }

  implicit def dtoToRawCommand2[A](implicit M: Monad[P])
  : SingleCommandResponseDecoder.Aux[P, RawCommandWithTempId[A], CommandResult, SingleCommandResultWithTempId] =
    fromCommandResultDtoDecoder[RawCommandWithTempId[A], SingleCommandResultWithTempId]((command, result) => for {
      r <- result.SyncStatus.get(command.uuid.toString)
      t <- result.TempIdMapping.flatMap(_.get(command.temp_id.toString))
    } yield SingleCommandResultWithTempId(r, t))

  implicit def rawCommandReturns1[A]: CommandReturns.Aux[RawCommand[A], SingleCommandResult] =
    new CommandReturns[RawCommand[A]] {
      type Result = SingleCommandResult
    }

  implicit def rawCommandReturns2[A]: CommandReturns.Aux[RawCommandWithTempId[A], SingleCommandResultWithTempId] =
    new CommandReturns[RawCommandWithTempId[A]] {
      type Result = SingleCommandResultWithTempId
    }
}
