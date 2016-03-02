package ru.pavkin.todoist.api.core.model

import ru.pavkin.todoist.api

import scala.util.Try

object Theme {

  val Todoist = Theme(0, "Todoist")
  val Noir = Theme(1, "Noir")
  val Neutral = Theme(2, "Neutral")
  val Tangerine = Theme(3, "Tangerine")
  val Sunflower = Theme(4, "Sunflower")
  val Clover = Theme(5, "Clover")
  val Blueberry = Theme(6, "Blueberry")
  val Sky = Theme(7, "Sky")
  val Amethyst = Theme(8, "Amethyst")
  val Graphite = Theme(9, "Graphite")

  private val themes = Vector(
    Todoist,
    Noir,
    Neutral,
    Tangerine,
    Sunflower,
    Clover,
    Blueberry,
    Sky,
    Amethyst,
    Graphite
  )

  def unsafeBy(n: Int): Theme =
    Try(themes(n)).getOrElse(api.unexpected)

}

case class Theme private(code: Int, name: String) {
  override def toString: String = name
}


