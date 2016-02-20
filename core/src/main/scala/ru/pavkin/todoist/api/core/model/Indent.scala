package ru.pavkin.todoist.api.core.model

object Indent {
  val Top = Indent(1)
  val Indent1 = Top
  val Indent2 = Indent(2)
  val Indent3 = Indent(3)
  val Indent4 = Indent(4)
}
case class Indent private(value: Int)
