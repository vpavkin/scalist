package ru.pavkin.todoist.api.core.model

import java.text.SimpleDateFormat
import java.util.{TimeZone, Date}

import scala.util.Try

object TodoistDate {

  private lazy val dateFormatter = {
    val formatter = new SimpleDateFormat("EEE dd MMM yyyy HH:mm:ss Z")
    formatter.setTimeZone(TimeZone.getTimeZone("UTC"))
    formatter
  }

  def parse(str: String): Option[Date] =
    Try(dateFormatter.parse(str)).toOption

  def format(date: Date): String = dateFormatter.format(date)
}
