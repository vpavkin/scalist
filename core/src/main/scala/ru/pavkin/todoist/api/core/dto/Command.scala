package ru.pavkin.todoist.api.core.dto

import java.util.UUID

trait ToRawCommandOps {
  implicit class Ops[A](o: A)(implicit T: ToRawCommand[A]) {
    def build: RawCommand[A] = T.build(o)
  }
}

trait ToRawCommand[T] {
  def build(o: T): RawCommand[T]
}

trait ToRawCommandWithTempId[T] {
  def withTempId(o: T, tempId: String): RawCommandWithTempId[T]
}

case class RawCommand[A](`type`: String, uuid: UUID, args: A)
case class RawCommandWithTempId[A](`type`: String, uuid: UUID, args: A, temp_id: UUID)

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

trait IsResourceId[T]
object IsResourceId {
  implicit val strResourceId: IsResourceId[String] = new IsResourceId[String] {}
  implicit val intResourceId: IsResourceId[Int] = new IsResourceId[Int] {}
}

case class UpdateProject[T: IsResourceId](id: Int,
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
