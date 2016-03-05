package ru.pavkin.todoist.api.core.dto

import ru.pavkin.todoist.api.core.IsResourceId

case class UpdateLabel[T: IsResourceId](id: T,
                                        name: Option[String] = None,
                                        color: Option[Int] = None,
                                        item_order: Option[Int] = None)
