package ru.pavkin.todoist.api.parser

import ru.pavkin.todoist.api.core.{IsResource, ReadResourceType}
import shapeless.HList

trait ParserProvider[P[_], S] {
  def parser[T <: ReadResourceType](implicit ITR: IsResource[T]): SingleResourceParser.Aux[P, S, ITR.Repr]
  def parser[T <: HList, R <: HList](implicit ITR: IsResource.Aux[T, R]): MultipleResourcesParser.Aux[P, S, ITR.Repr]
}
