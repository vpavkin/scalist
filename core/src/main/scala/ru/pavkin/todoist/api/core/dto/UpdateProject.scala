package ru.pavkin.todoist.api.core.dto

import ru.pavkin.todoist.api.core.IsResourceId

case class UpdateProject[T: IsResourceId](id: T,
                                          name: Option[String] = None,
                                          color: Option[Int] = None,
                                          indent: Option[Int] = None,
                                          item_order: Option[Int] = None,
                                          collapsed: Option[Int] = None)
