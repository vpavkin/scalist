package ru.pavkin.todoist.api.core

import ru.pavkin.todoist.api.parser.{MultipleResourcesParser, SingleResourceParser}
import shapeless._

trait API[F[_], P[_], Base] {
  def get[R](implicit
             IR: IsResource[R],
             parser: SingleResourceParser.Aux[P, Base, R]): SingleReadResourceDefinition[F, P, R, Base]

  def getAll[R <: HList](implicit
                         IR: IsResource[R],
                         parser: MultipleResourcesParser.Aux[P, Base, R]): MultipleReadResourceDefinition[F, P, R, Base]
}







