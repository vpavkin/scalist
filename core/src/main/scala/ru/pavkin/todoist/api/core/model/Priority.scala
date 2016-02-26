package ru.pavkin.todoist.api.core.model

object Priority {
  val Highest = Priority(4)
  val Lowest = Priority(1)

  val level1 = Highest
  val level2 = Priority(3)
  val level3 = Priority(2)
  val level4 = Lowest
}
case class Priority private(level: Int)
