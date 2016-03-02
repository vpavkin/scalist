package ru.pavkin.todoist.api.core.dto

case class Reminder(id: Int,
                    notify_uid: Int,
                    item_id: Int,
                    service: Option[String],
                    `type`: String,
                    date_string: Option[String],
                    date_lang: Option[String],
                    due_date_utc: Option[String],
                    mm_offset: Option[Int],
                    minute_offset: Option[Int],
                    name: Option[String],
                    loc_lat: Option[String],
                    loc_long: Option[String],
                    loc_trigger: Option[String],
                    radius: Option[Int],
                    is_deleted: Int)
