package ru.pavkin.todoist.api.core.dto

import ru.pavkin.todoist.api.core.IsResourceId

case class AddReminder[T: IsResourceId](item_id: T,
                                        `type`: String,
                                        notify_uid: Option[Int] = None,
                                        service: Option[String] = None,
                                        date_string: Option[String] = None,
                                        date_lang: Option[String] = None,
                                        due_date_utc: Option[String] = None,
                                        minute_offset: Option[Int] = None,
                                        name: Option[String] = None,
                                        loc_lat: Option[String] = None,
                                        loc_long: Option[String] = None,
                                        loc_trigger: Option[String] = None,
                                        radius: Option[Int] = None)
