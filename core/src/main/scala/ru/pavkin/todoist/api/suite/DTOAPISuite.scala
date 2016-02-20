package ru.pavkin.todoist.api.suite

import cats.Monad
import ru.pavkin.todoist.api.core.decoder.SingleResponseDecoder
import ru.pavkin.todoist.api.core.dto
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

}
