package ru.pavkin.todoist.api.core.dto

case class Task(id: Int,
                user_id: Int,
                project_id: Int,
                content: String,
                date_string: String,
                date_lang: String,
                due_date_utc: Option[String], // YYYY-MM-DDTHH:MM, UTC strictly
                priority: Int,
                indent: Int,
                item_order: Int,
                day_order: Int,
                collapsed: Int,
                labels: List[Int],
                assigned_by_uid: Option[Int] = None, // only for shared
                responsible_uid: Option[Int] = None, // only for shared
                checked: Int,
                in_history: Int,
                is_deleted: Int,
                is_archived: Int,
                date_added: String)
