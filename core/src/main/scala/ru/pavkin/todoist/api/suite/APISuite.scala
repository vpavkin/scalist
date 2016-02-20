package ru.pavkin.todoist.api.suite

import ru.pavkin.todoist.api.core.HasRawRequest
import shapeless.{::, HNil}

trait APISuite[F[_], P[_], Base] {

  type Projects
  type Labels
  type All = Projects :: Labels :: HNil

  implicit val projects = HasRawRequest.resource[Projects](List("projects"))
  implicit val labels = HasRawRequest.resource[Labels](List("labels"))
  implicit val all = HasRawRequest.resource[All](List("all"))

}
