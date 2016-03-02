package ru.pavkin.todoist.api.core.model

import ru.pavkin.todoist.api

object DayOfWeek {
  val Monday = DayOfWeek(1, "Monday")
  val Tuesday = DayOfWeek(2, "Tuesday")
  val Wednesday = DayOfWeek(3, "Wednesday")
  val Thursday = DayOfWeek(4, "Thursday")
  val Friday = DayOfWeek(5, "Friday")
  val Saturday = DayOfWeek(6, "Saturday")
  val Sunday = DayOfWeek(7, "Sunday")

  private lazy val daysMap = Vector(
    Monday,
    Tuesday,
    Wednesday,
    Thursday,
    Friday,
    Saturday,
    Sunday
  ).map(i => i.code -> i).toMap

  def unsafeBy(n: Int): DayOfWeek =
    daysMap.getOrElse(n, api.unexpected)
}

case class DayOfWeek private(code: Int, name: String){
  override def toString: String = name
}
