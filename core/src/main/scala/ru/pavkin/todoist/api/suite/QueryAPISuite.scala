package ru.pavkin.todoist.api.suite

import ru.pavkin.todoist.api.core.HasRawRequest
import shapeless.ops.hlist.Selector
import shapeless.{HList, ::, HNil}

trait QueryAPISuite {

  type Projects
  type Labels
  type All = Projects :: Labels :: HNil

  implicit val projects = HasRawRequest.resource[Projects](List("projects"))
  implicit val labels = HasRawRequest.resource[Labels](List("labels"))
  implicit val all = HasRawRequest.resource[All](List("all"))

  object syntax {
    implicit class HListOps[L <: HList](l: L) {
      def projects(implicit S: Selector[L, Projects]): Projects = S(l)
      def labels(implicit S: Selector[L, Labels]): Labels = S(l)
    }
  }
}
