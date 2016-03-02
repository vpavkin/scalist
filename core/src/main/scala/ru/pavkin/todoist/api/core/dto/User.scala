package ru.pavkin.todoist.api.core.dto

case class TimeZoneOffset(gmtString: String, hours: Int, minutes: Int)

case class User(id: Int,
                email: String,
                full_name: String,
                inbox_project: Int,
                timezone: String,
                tz_offset: TimeZoneOffset,
                start_page: String,
                start_day: Int,
                next_week: Int,
                time_format: Int,
                date_format: Int,
                sort_order: Int,
                has_push_reminders: Boolean,
                default_reminder: Option[String],
                auto_reminder: Option[Int],
                mobile_number: Option[String],
                mobile_host: Option[String],
                completed_count: Int,
                completed_today: Int,
                karma: Double,
                karma_trend: String, //todo: need a well-defined set of possible values
                is_premium: Boolean,
                premium_until: Option[String],
                is_biz_admin: Boolean,
                business_account_id: Option[Int],
                image_id: Option[String], //todo: is it useful at all?
                beta: Int,
                is_dummy: Int,
                join_date: String,
                theme: Int,
                avatar_small: Option[String],
                avatar_medium: Option[String],
                avatar_big: Option[String],
                avatar_s640: Option[String])
