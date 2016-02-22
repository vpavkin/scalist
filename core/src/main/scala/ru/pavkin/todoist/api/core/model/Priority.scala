package ru.pavkin.todoist.api.core.model

object Priority {
  val Highest = Priority(4)
  val Lowest = Priority(1)

  val Priority1 = Highest
  val Priority2 = Priority(3)
  val Priority3 = Priority(2)
  val Priority4 = Lowest
}
case class Priority private(value: Int)
