package ru.pavkin.todoist.api.suite

trait AbstractDTOAPISuite[P[_]] {

  def dtoDecodingError[T](msg: String): P[T]
}
