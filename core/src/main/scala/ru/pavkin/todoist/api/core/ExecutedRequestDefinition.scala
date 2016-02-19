package ru.pavkin.todoist.api.core

import cats.Functor
import cats.syntax.functor._

trait ExecutedRequestDefinition[F[_], L[_], P[_], R, Req, Base] extends RequestDefinition[F, P, R, Base] {

  type Out = R

  def execute: F[Out] = flatten(load.map(parse))

  def load: L[Base]
  def flatten(r: L[P[Out]]): F[Out]
  def parse(r: Base): P[Out]

  implicit def F: Functor[L]
}
