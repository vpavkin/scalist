package ru.pavkin.todoist.api.core.dto

case class AddTaskToInbox(content: String,
                          date_string: Option[String] = None,
                          date_lang: Option[String] = None,
                          due_date_utc: Option[String] = None,
                          priority: Option[Int] = None,
                          indent: Option[Int] = None,
                          item_order: Option[Int] = None,
                          day_order: Option[Int] = None,
                          collapsed: Option[Int] = None,
                          labels: List[Int] = Nil)
