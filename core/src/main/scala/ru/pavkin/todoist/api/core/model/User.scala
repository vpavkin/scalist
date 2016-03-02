package ru.pavkin.todoist.api.core.model

import java.util.{Date, TimeZone}

import ru.pavkin.todoist.api.core.tags
import shapeless.tag.@@

case class UserAvatars(small: Option[String],
                       medium: Option[String],
                       big: Option[String],
                       s640: Option[String])

case class User(id: Int @@ tags.UserId,
                email: String,
                fullName: String,
                inboxProject: Int @@ tags.ProjectId,
                timezone: TimeZone,
                startPageQuery: String,
                weekStartDay: DayOfWeek,
                postponeTo: DayOfWeek,
                timeFormat: TimeFormat,
                dateFormat: DateFormat,
                projectsSortOrder: ProjectsSortOrder,
                hasPushReminders: Boolean,
                defaultReminder: Option[ReminderService],
                autoReminder: Option[Int],
                mobileNumber: Option[String],
                mobileHost: Option[String],
                totalCompletedTasksCount: Int,
                todayCompletedTasksCount: Int,
                karma: Double,
                premiumUntil: Option[Date],
                isBusinessAccountAdmin: Boolean,
                businessAccountId: Option[Int],
                isBeta: Boolean,
                isDummy: Boolean,
                dateJoined: Date,
                theme: Theme,
                avatars: UserAvatars)
