package ru.pavkin.todoist.api.core

import shapeless.{::, HNil}

trait APISuite {

  type Projects
  type Labels
  type All = Projects :: Labels :: HNil

  implicit val projects = IsResource[Projects](Vector("projects"))
  implicit val labels = IsResource[Labels](Vector("labels"))
  implicit val all = IsResource[All](Vector("all"))

}
