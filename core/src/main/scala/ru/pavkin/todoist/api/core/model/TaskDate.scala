package ru.pavkin.todoist.api.core.model

import java.util.Date

case class TaskDate(text: String,
                    language: DateLanguage,
                    dueDateUTC: Date)

// todo: full language names
object DateLanguage {
  val en = DateLanguage("en")
  val da = DateLanguage("da")
  val pl = DateLanguage("pl")
  val zh = DateLanguage("zh")
  val ko = DateLanguage("ko")
  val de = DateLanguage("de")
  val pt = DateLanguage("pt")
  val ja = DateLanguage("ja")
  val it = DateLanguage("it")
  val fr = DateLanguage("fr")
  val sv = DateLanguage("sv")
  val ru = DateLanguage("ru")
  val es = DateLanguage("es")
  val nl = DateLanguage("nl")
}

case class DateLanguage private(value: String)
