package ru.pavkin.todoist.api.core.model

import ru.pavkin.todoist.api

object TimeFormat {
  val h24 = TimeFormat(0, "24h")
  val h12 = TimeFormat(1, "12h")

  private lazy val timeFormatsMap = Vector(
    h24,
    h12
  ).map(i => i.code -> i).toMap

  def unsafeBy(n: Int): TimeFormat =
    timeFormatsMap.getOrElse(n, api.unexpected)
}

case class TimeFormat private(code: Int, format: String) {
  override def toString: String = format
}
