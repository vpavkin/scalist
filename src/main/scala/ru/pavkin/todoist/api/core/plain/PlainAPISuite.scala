package ru.pavkin.todoist.api.core.plain

import cats._
import ru.pavkin.todoist.api._
import ru.pavkin.todoist.api.core.parser.SingleResourceParser
import ru.pavkin.todoist.api.core.{APISuite, tags}

trait PlainAPISuite[A] extends APISuite {

  type Projects = tags.Projects
  type Labels = tags.Labels

  implicit def dummyParser[T]: SingleResourceParser.Aux[Id, A, T] =
    SingleResourceParser.using[Id, A, T](_ => unexpected[T])

}
