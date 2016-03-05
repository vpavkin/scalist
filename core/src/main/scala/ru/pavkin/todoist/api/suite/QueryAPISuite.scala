package ru.pavkin.todoist.api.suite

import ru.pavkin.todoist.api.core.HasRawRequest
import shapeless.ops.hlist.Selector
import shapeless.{HList, ::, HNil}

trait QueryAPISuite {

  /**
    * Collection of `Project` entities
    */
  type Projects
  /**
    * Collection of `Label` entities
    */
  type Labels
  /**
    * Collection of `Item` entities
    */
  type Tasks
  /**
    * Collection of `Note` entities
    */
  type Notes
  /**
    * Collection of `Filter` entities
    */
  type Filters
  /**
    * Collection of `Reminder` entities
    */
  type Reminders
  /**
    * `User` entity
    */
  type User
  /**
    * All resources that can be requested
    */
  type All = User :: Reminders :: Filters :: Notes :: Tasks :: Projects :: Labels :: HNil

  implicit val tasks = HasRawRequest.resource[Tasks](List("items"))
  implicit val projects = HasRawRequest.resource[Projects](List("projects"))
  implicit val labels = HasRawRequest.resource[Labels](List("labels"))
  implicit val notes = HasRawRequest.resource[Notes](List("notes"))
  implicit val filters = HasRawRequest.resource[Filters](List("filters"))
  implicit val reminders = HasRawRequest.resource[Reminders](List("reminders"))
  implicit val user = HasRawRequest.resource[User](List("user"))
  implicit val all = HasRawRequest.resource[All](List("all"))

  trait QuerySyntax {
    implicit class HListQueryOps[L <: HList](l: L) {
      /**
        * Returns the list of projects contained in this response
        *
        * @example {{{res.projects}}}
        */
      def projects(implicit S: Selector[L, Projects]): Projects = S(l)
      /**
        * Returns the list of labels contained in this response
        */
      def labels(implicit S: Selector[L, Labels]): Labels = S(l)
      /**
        * Returns the list of tasks contained in this response
        */
      def tasks(implicit S: Selector[L, Tasks]): Tasks = S(l)
      /**
        * Returns the list of notes contained in this response
        */
      def notes(implicit S: Selector[L, Notes]): Notes = S(l)
      /**
        * Returns the list of filters contained in this response
        */
      def filters(implicit S: Selector[L, Filters]): Filters = S(l)
      /**
        * Returns the list of reminders contained in this response
        */
      def reminders(implicit S: Selector[L, Reminders]): Reminders = S(l)
      /**
        * Returns the user object contained in this response
        */
      def user(implicit S: Selector[L, User]): User = S(l)
    }
  }
}
