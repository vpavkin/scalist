package ru.pavkin.todoist.api.suite

import ru.pavkin.todoist.api.core.HasRawRequest
import shapeless.ops.hlist.Selector
import shapeless.{HList, ::, HNil}

trait QueryAPISuite {

  type Projects
  type Labels
  type Tasks
  type Notes
  type Filters
  type Reminders
  type All = Reminders :: Filters :: Notes :: Tasks :: Projects :: Labels :: HNil

  implicit val tasks = HasRawRequest.resource[Tasks](List("items"))
  implicit val projects = HasRawRequest.resource[Projects](List("projects"))
  implicit val labels = HasRawRequest.resource[Labels](List("labels"))
  implicit val notes = HasRawRequest.resource[Notes](List("notes"))
  implicit val filters = HasRawRequest.resource[Filters](List("filters"))
  implicit val reminders = HasRawRequest.resource[Reminders](List("reminders"))
  implicit val all = HasRawRequest.resource[All](List("all"))

  trait QuerySyntax {
    implicit class HListQueryOps[L <: HList](l: L) {
      def projects(implicit S: Selector[L, Projects]): Projects = S(l)
      def labels(implicit S: Selector[L, Labels]): Labels = S(l)
      def tasks(implicit S: Selector[L, Tasks]): Tasks = S(l)
      def notes(implicit S: Selector[L, Notes]): Notes = S(l)
      def filters(implicit S: Selector[L, Filters]): Filters = S(l)
      def reminders(implicit S: Selector[L, Reminders]): Reminders = S(l)
    }
  }
}
