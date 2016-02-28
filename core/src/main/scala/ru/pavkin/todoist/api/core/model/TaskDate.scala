package ru.pavkin.todoist.api.core.model

import java.util.Date

import ru.pavkin.todoist.api

// todo: incorporate xx:xx:59 thing
case class TaskDate(text: Option[String],
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

  private lazy val langs: Map[String, DateLanguage] = List(
    en, da, pl, zh, ko, de, pt, ja, it, fr, sv, ru, es, nl
  ).map(l => l.code -> l).toMap

  def unsafeBy(code: String): DateLanguage =
    langs.getOrElse(code, api.unexpected)
}

case class DateLanguage private(code: String)
