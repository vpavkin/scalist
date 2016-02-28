package ru.pavkin.todoist.api.core.model

import ru.pavkin.todoist.api

object Priority {
  val Highest = Priority(4)
  val Lowest = Priority(1)

  val level1 = Highest
  val level2 = Priority(3)
  val level3 = Priority(2)
  val level4 = Lowest

  private lazy val levels =
    List(level1, level2, level3, level4).map(l => l.level -> l).toMap

  def unsafeBy(level: Int) = levels.getOrElse(level, api.unexpected)
}
case class Priority private(level: Int)
