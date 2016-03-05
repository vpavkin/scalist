package ru.pavkin.todoist.api.core.dto

import ru.pavkin.todoist.api.core.IsResourceId

case class UpdateFilter[T: IsResourceId](id: T,
                                         name: Option[String] = None,
                                         query: Option[String] = None,
                                         color: Option[Int] = None,
                                         order: Option[Int] = None)
