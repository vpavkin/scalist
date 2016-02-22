package ru.pavkin.todoist.api.core.model.util

import ru.pavkin.todoist.api.core.model.Command
import shapeless.{::, HNil}

object CombineCommands {
  trait Syntax {
    implicit class CombineCommandsOps[C <: Command](command: C) {
      def :+[A](other: A): C :: A :: HNil = command :: other :: HNil
    }
  }
}
