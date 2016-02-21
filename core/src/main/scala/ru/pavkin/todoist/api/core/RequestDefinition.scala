package ru.pavkin.todoist.api.core

trait RequestDefinition[F[_], P[_], R, Base] {
  def execute: F[R]
}




