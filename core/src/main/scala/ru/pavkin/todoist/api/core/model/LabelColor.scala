package ru.pavkin.todoist.api.core.model

object LabelColor {
  val color0 = LabelColor(0, 0x019412)
  val color1 = LabelColor(1, 0xa39d01)
  val color2 = LabelColor(2, 0xe73d02)
  val color3 = LabelColor(3, 0xe702a4)
  val color4 = LabelColor(4, 0x9902e7)
  val color5 = LabelColor(5, 0x1d02e7)
  val color6 = LabelColor(6, 0x0082c5)
  val color7 = LabelColor(7, 0x555555)

  val color8 = LabelColor(8, 0x008299)
  val color9 = LabelColor(9, 0x03b3b2)
  val color10 = LabelColor(10, 0xac193d)
  val color11 = LabelColor(11, 0x82ba00)
  val color12 = LabelColor(12, 0x111111)
}

case class LabelColor private(code: Int, value: Int) {
  def isPremium: Boolean = code >= 8
}
