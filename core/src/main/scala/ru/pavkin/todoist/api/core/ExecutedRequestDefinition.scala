package ru.pavkin.todoist.api.core

import cats.Functor
import cats.syntax.functor._

trait ExecutedRequestDefinition[F[_], L[_], P[_], R, Req, Base] extends RequestDefinition[F, P, R, Base] {

  def execute: F[R] = flatten(load.map(parse))

  def load: L[Base]
  def flatten(r: L[P[R]]): F[R]
  def parse(r: Base): P[R]

  implicit def F: Functor[L]
}
