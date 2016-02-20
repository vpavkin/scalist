package ru.pavkin.todoist.api.core

import cats.Monad
import ru.pavkin.todoist.api.core.decoder.SingleResponseDecoder
import ru.pavkin.todoist.api.core.dto.{AllResources, Label, Project}

trait DTOAPISuite[F[_], P[_], Base] extends AbstractDTOAPISuite[F, P, Base, AllResources] {

  type Projects = List[Project]
  type Labels = List[Label]

  implicit def dtoToProjects(implicit M: Monad[P]): SingleResponseDecoder.Aux[P, AllResources, Projects] =
    fromDtoDecoder(_.Projects)("projects")

  implicit def dtoToLabels(implicit M: Monad[P]): SingleResponseDecoder.Aux[P, AllResources, Labels] =
    fromDtoDecoder(_.Labels)("labels")
}
