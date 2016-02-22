package ru.pavkin.todoist.api.core.dto

import java.util.UUID

import ru.pavkin.todoist.api.core.IsResourceId

case class RawCommand[A](`type`: String, uuid: UUID, args: A)
case class RawTempIdCommand[A](`type`: String, uuid: UUID, args: A, temp_id: UUID)

case class AddProject(name: String,
                      color: Option[Int] = None,
                      indent: Option[Int] = None,
                      item_order: Option[Int] = None)

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

case class AddTask[T: IsResourceId](content: String,
                                    project_id: T,
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

case class AddLabel(name: String,
                    color: Option[Int] = None,
                    item_order: Option[Int] = None)

case class UpdateProject[T: IsResourceId](id: T,
                                          name: Option[String] = None,
                                          color: Option[Int] = None,
                                          indent: Option[Int] = None,
                                          item_order: Option[Int] = None,
                                          collapsed: Option[Int] = None)

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

case class UpdateLabel[T: IsResourceId](id: T,
                                        name: Option[String] = None,
                                        color: Option[Int] = None,
                                        item_order: Option[Int] = None)
