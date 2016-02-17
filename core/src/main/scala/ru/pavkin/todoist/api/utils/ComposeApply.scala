package ru.pavkin.todoist.api.utils

import cats.Apply

trait ComposeApply {

  implicit def composeApply[F[_], G[_]](implicit FF: Apply[F], GG: Apply[G]): Apply[({type L[X] = F[G[X]]})#L] =
    FF.compose(GG)
}

