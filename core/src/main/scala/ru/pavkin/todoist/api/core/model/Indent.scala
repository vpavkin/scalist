package ru.pavkin.todoist.api.core.model

import ru.pavkin.todoist.api

import scala.util.Try

object Indent {
  val Top = Indent(1)
  val Indent1 = Top
  val Indent2 = Indent(2)
  val Indent3 = Indent(3)
  val Indent4 = Indent(4)

  private lazy val indentsMap = Vector(
    Indent1,
    Indent2,
    Indent3,
    Indent4
  ).map(i => i.value -> i).toMap

  def unsafeBy(n: Int): Indent =
    indentsMap.getOrElse(n, api.unexpected)
}
case class Indent private(value: Int)
