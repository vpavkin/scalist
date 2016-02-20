package ru.pavkin.todoist.api.core.model

object Priority {
  val Top = Priority(1)
  val Priority1 = Top
  val Priority2 = Priority(2)
  val Priority3 = Priority(3)
  val Priority4 = Priority(4)
}
case class Priority private(value: Int)
