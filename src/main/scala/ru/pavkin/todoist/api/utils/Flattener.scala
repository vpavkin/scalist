package ru.pavkin.todoist.api.utils

trait Flattener[F[_], L[_], P[_]] {
  def flatten[T](o: L[P[T]]): F[T]
}
