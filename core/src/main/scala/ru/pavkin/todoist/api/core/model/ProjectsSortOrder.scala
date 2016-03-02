package ru.pavkin.todoist.api.core.model

import ru.pavkin.todoist.api

object ProjectsSortOrder {
  val oldestFirst = ProjectsSortOrder(0, "oldest first")
  val newestFirst = ProjectsSortOrder(1, "newest first")

  private lazy val projectsSortOrders = Vector(
    oldestFirst,
    newestFirst
  ).map(i => i.code -> i).toMap

  def unsafeBy(n: Int): ProjectsSortOrder =
    projectsSortOrders.getOrElse(n, api.unexpected)
}

case class ProjectsSortOrder private(code: Int, name: String) {
  override def toString: String = name
}
