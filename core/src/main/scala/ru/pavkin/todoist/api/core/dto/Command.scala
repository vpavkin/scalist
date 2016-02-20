package ru.pavkin.todoist.api.core.dto

import java.util.UUID

case class Command[A](`type`: String, uuid: UUID, args: A, temp_id: Option[String])

case class AddProject(name: String,
                      color: Option[Int] = None,
                      indent: Option[Int] = None,
                      item_order: Option[Int] = None)

// todo: try reuse update project
case class UpdateProjectTempId(id: String,
                               name: String,
                               color: Option[Int] = None,
                               indent: Option[Int] = None,
                               item_order: Option[Int] = None,
                               collapsed: Option[Int] = None)

case class UpdateProject(id: Int,
                         name: String,
                         color: Option[Int] = None,
                         indent: Option[Int] = None,
                         item_order: Option[Int] = None,
                         collapsed: Option[Int] = None)


case class AddItemTempId(content: String,
                         project_id: Option[String] = None,
                         date_string: Option[String] = None, // empty string to unset
                         date_lang: Option[String] = None,
                         due_date_utc: Option[String] = None, // YYYY-MM-DDTHH:MM, UTC strictly
                         priority: Option[Int] = None,
                         indent: Option[Int] = None,
                         item_order: Option[Int] = None,
                         day_order: Option[Int] = None,
                         collapsed: Option[Int] = None,
                         labels: List[Int] = Nil,
                         assigned_by_uid: Option[Int] = None, // only for shared
                         responsible_uid: Option[Int] = None // only for shared
                        )

case class AddTask(content: String,
                   project_id: Option[Int] = None,
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

case class UpdateItemTempId(id: String,
                            content: Option[String] = None,
                            project_id: Option[String] = None,
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

case class UpdateItem(id: Int,
                      content: Option[String] = None,
                      project_id: Option[String] = None,
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
