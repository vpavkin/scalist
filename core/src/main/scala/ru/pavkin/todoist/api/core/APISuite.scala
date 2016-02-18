package ru.pavkin.todoist.api.core

import ru.pavkin.todoist.api.core.parser.SingleResourceParser
import shapeless.{::, HNil}

trait APISuite[F[_], P[_], Base] {

  type Projects
  type Labels
  type All = Projects :: Labels :: HNil

  implicit val projects = HasRawRequest[Projects](Vector("projects"))
  implicit val labels = HasRawRequest[Labels](Vector("labels"))
  implicit val all = HasRawRequest[All](Vector("all"))

  implicit def projectsParser: SingleResourceParser.Aux[P, Base, Projects]
  implicit def labelsParser: SingleResourceParser.Aux[P, Base, Labels]
}
