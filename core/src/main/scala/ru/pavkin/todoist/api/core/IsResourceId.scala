package ru.pavkin.todoist.api.core

import java.util.UUID

sealed trait IsResourceId[T]

object IsResourceId {
  implicit val uuidResourceId: IsResourceId[UUID] = new IsResourceId[UUID] {}
  implicit val intResourceId: IsResourceId[Int] = new IsResourceId[Int] {}
}
