package ru.pavkin.todoist.api.parser

import cats.Functor
import cats.syntax.functor._
import ru.pavkin.todoist.api.core.RequestDefinition

trait ParsedBasedRequestDefinition[F[_], L[_], P[_], R, Req, Base] extends RequestDefinition[F, P, R, Base] {

  type Out = R

  def execute: F[Out] = flatten(load.map(parse _))

  def load: L[Base]
  def flatten(r: L[P[Out]]): F[Out]
  def parse(r: Base): P[Out]

  implicit def F: Functor[L]
}
