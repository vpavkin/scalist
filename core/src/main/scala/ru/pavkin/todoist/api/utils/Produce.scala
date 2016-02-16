package ru.pavkin.todoist.api.utils

trait Produce[A, B] {
  def produce(a: A): B
}

object Produce {
  def apply[A, B](f: A => B): Produce[A, B] = new Produce[A, B] {
    def produce(a: A): B = f(a)
  }
}
