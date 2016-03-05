package ru.pavkin.todoist.api.core.dto

import ru.pavkin.todoist.api.core.IsResourceId

case class UpdateTask[T: IsResourceId](id: T,
                                       content: Option[String] = None,
                                       date_string: Option[String] = None,
                                       date_lang: Option[String] = None,
                                       due_date_utc: Option[String] = None,
                                       priority: Option[Int] = None,
                                       indent: Option[Int] = None,
                                       item_order: Option[Int] = None,
                                       day_order: Option[Int] = None,
                                       collapsed: Option[Int] = None,
                                       labels: List[Int] = Nil,
                                       assigned_by_uid: Option[Int] = None,
                                       responsible_uid: Option[Int] = None)
