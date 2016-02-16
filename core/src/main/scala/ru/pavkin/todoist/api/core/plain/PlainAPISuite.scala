package ru.pavkin.todoist.api.core.plain

import cats._
import ru.pavkin.todoist.api._
import ru.pavkin.todoist.api.core.parser.SingleResourceParser
import ru.pavkin.todoist.api.core.{APISuite, tags}

trait PlainAPISuite[A, F[_]] extends APISuite[F, Id, A] {

  type Projects = tags.Projects
  type Labels = tags.Labels

  implicit def dummyParser[T]: SingleResourceParser.Aux[Id, A, T] =
    SingleResourceParser.using[Id, A, T](_ => unexpected[T])

  override implicit val projectsParser: SingleResourceParser.Aux[Id, A, tags.Projects] =
    implicitly[SingleResourceParser.Aux[Id, A, Projects] ]
  override implicit val labelsParser: SingleResourceParser.Aux[Id, A, tags.Labels] =
    implicitly[SingleResourceParser.Aux[Id, A, Labels] ]
}
