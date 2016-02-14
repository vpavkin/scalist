package ru.pavkin.todoist.api.utils

trait Produce[A, B] {
  def produce(a: A): B
}
