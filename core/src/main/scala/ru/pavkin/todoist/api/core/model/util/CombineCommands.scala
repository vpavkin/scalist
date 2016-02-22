package ru.pavkin.todoist.api.core.model.util

import ru.pavkin.todoist.api.core.model.Command
import shapeless.{::, HNil}

trait CombineCommands[C] {
  def and[A](command: C, other: A): C :: A :: HNil =
    command :: other :: HNil
}

object CombineCommands {
  trait Syntax {
    implicit class CombineCommandsOps[C](command: C)(implicit C: CombineCommands[C]) {
      def :+[A](other: A): C :: A :: HNil = C.and(command, other)
    }
  }

  implicit def commandsCombine[C <: Command]: CombineCommands[C] = new CombineCommands[C] {}
}
