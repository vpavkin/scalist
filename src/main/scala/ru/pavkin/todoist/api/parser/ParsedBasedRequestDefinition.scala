package ru.pavkin.todoist.api.parser

import cats.Functor
import cats.syntax.functor._
import ru.pavkin.todoist.api.core.RequestDefinition

trait ParsedBasedRequestDefinition[F[_], L[_], P[_], Res0, Req] extends RequestDefinition[F] {

  def execute: F[Res] = flatten(load.map(parse _))

  def load: L[Res0]
  def flatten(r: L[P[Res]]): F[Res]
  def parse(r: Res0): P[Res]

  implicit def F: Functor[L]
}
