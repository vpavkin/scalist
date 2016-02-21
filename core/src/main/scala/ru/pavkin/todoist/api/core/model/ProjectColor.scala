package ru.pavkin.todoist.api.core.model

import ru.pavkin.todoist.api

import scala.util.Try

object ProjectColor {

  val color0 = ProjectColor(0, 0x95ef63)
  val color1 = ProjectColor(1, 0xff8581)
  val color2 = ProjectColor(2, 0xffc471)
  val color3 = ProjectColor(3, 0xf9ec75)
  val color4 = ProjectColor(4, 0xa8c8e4)
  val color5 = ProjectColor(5, 0xd2b8a3)
  val color6 = ProjectColor(6, 0xe2a8e4)
  val color7 = ProjectColor(7, 0xcccccc)
  val color8 = ProjectColor(8, 0xfb886e)
  val color9 = ProjectColor(9, 0xffcc00)
  val color10 = ProjectColor(10, 0x74e8d3)
  val color11 = ProjectColor(11, 0x3bd5fb)

  val color12 = ProjectColor(12, 0xdc4fad)
  val color13 = ProjectColor(13, 0xac193d)
  val color14 = ProjectColor(14, 0xd24726)
  val color15 = ProjectColor(15, 0x82ba00)
  val color16 = ProjectColor(16, 0x03b3b2)
  val color17 = ProjectColor(17, 0x008299)
  val color18 = ProjectColor(18, 0x5db2ff)
  val color19 = ProjectColor(19, 0x0072c6)
  val color20 = ProjectColor(20, 0x000000)
  val color21 = ProjectColor(21, 0x777777)

  private val colors = Vector(
    color0,
    color1,
    color2,
    color3,
    color4,
    color5,
    color6,
    color7,
    color8,
    color9,
    color10,
    color11,
    color12,
    color13,
    color14,
    color15,
    color16,
    color17,
    color18,
    color19,
    color20,
    color21)

  def apply(n: Int): ProjectColor =
    Try(colors(n)).getOrElse(api.unexpected)

}

case class ProjectColor private(code: Int, value: Int) {
  def isPremium: Boolean = code >= 12
  override def toString: String = s"ProjectColor($code, 0x${value.toHexString})"
}
