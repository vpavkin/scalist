package ru.pavkin.todoist.api.core

import shapeless.{HNil, ::}

sealed trait ReadResourceType

object ReadResourceType {
  trait Projects extends ReadResourceType
  trait Labels extends ReadResourceType
  type All = Projects :: Labels :: HNil
}
