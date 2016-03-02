package ru.pavkin.todoist.api.core.model

import ru.pavkin.todoist.api

object DateFormat {
  val DDMMYY = DateFormat(0, "DD-MM-YYYY")
  val MMDDYY = DateFormat(1, "MM-DD-YYYY")

  private lazy val dateFormatsMap = Vector(
    DDMMYY,
    MMDDYY
  ).map(i => i.code -> i).toMap

  def unsafeBy(n: Int): DateFormat =
    dateFormatsMap.getOrElse(n, api.unexpected)
}

case class DateFormat private(code: Int, format: String) {
  override def toString: String = format
}
