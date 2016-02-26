package ru.pavkin.todoist.api.core.model

import ru.pavkin.todoist.api

import scala.util.Try

object Indent {
  val Top = Indent(1)
  val level1 = Top
  val level2 = Indent(2)
  val level3 = Indent(3)
  val level4 = Indent(4)

  private lazy val indentsMap = Vector(
    level1,
    level2,
    level3,
    level4
  ).map(i => i.code -> i).toMap

  def unsafeBy(n: Int): Indent =
    indentsMap.getOrElse(n, api.unexpected)
}
case class Indent private(code: Int)
