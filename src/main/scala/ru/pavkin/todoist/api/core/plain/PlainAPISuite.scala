package ru.pavkin.todoist.api.core.plain

import cats._
import ru.pavkin.todoist.api._
import ru.pavkin.todoist.api.core.{APISuite, tags}
import ru.pavkin.todoist.api.parser.{MultipleResourcesParser, SingleResourceParser}
import shapeless.HList

trait PlainAPISuite[A] extends APISuite {

  type Projects = tags.Projects
  type Labels = tags.Labels

  implicit def dummySingleParser[T]: SingleResourceParser.Aux[Id, A, T] =
    SingleResourceParser.using[Id, A, T](_ => unexpected[T])

  implicit def dummyMultipleParser[T <: HList]: MultipleResourcesParser.Aux[Id, A, T] =
    MultipleResourcesParser.using[Id, A, T](_ => unexpected[T])
}
