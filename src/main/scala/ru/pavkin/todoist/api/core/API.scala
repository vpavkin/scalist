package ru.pavkin.todoist.api.core

import ru.pavkin.todoist.api.core.ReadResourceType._
import shapeless._

trait API[F[_]] {
  def get[RType <: ReadResourceType](implicit ITR: IsResource[RType]): SingleReadResourceDefinition[F, RType, ITR.Repr]
  def getAll[Result <: HList](implicit ITR: IsResource.Aux[All, Result]): MultipleReadResourceDefinition[F, All, Result]
}







