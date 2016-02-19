package ru.pavkin.todoist.api.core

trait RequestDefinition[F[_], P[_], R, Base] {
  type Out

  def execute: F[Out]
}




