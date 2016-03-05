package ru.pavkin.todoist.api.core

trait RequestDefinition[F[_], P[_], R, Base] {

  /**
    * Executes this request definition and returns the result wrapped with the API effect
    */
  def execute: F[R]
}




