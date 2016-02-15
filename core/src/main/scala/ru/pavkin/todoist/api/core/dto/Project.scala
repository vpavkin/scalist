package ru.pavkin.todoist.api.core.dto

case class Project(id: Int,
                   user_id: Int,
                   name: String,
                   color: Int,
                   indent: Int,
                   item_order: Int,
                   collapsed: Int,
                   shared: Boolean,
                   is_deleted: Int,
                   is_archived: Int,
                   archived_date: Option[String],
                   archived_timestamp: Int,
                   inbox_project: Option[Boolean],
                   team_inbox: Option[Boolean])


