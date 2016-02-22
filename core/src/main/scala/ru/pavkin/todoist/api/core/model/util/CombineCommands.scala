package ru.pavkin.todoist.api.core.model.util

import java.util.UUID

import ru.pavkin.todoist.api.core.model.{TempIdCommand, Command}
import shapeless.tag.@@
import shapeless.{HList, ::, HNil}

object CombineCommands {
  trait Syntax {
    implicit class CombineCommandsOps[C <: Command](command: C) {
      def :+[A](other: A): C :: A :: HNil = command :: other :: HNil
    }

    implicit class TempIdProduceCommandsOps[Tag, C](command: C)(implicit ev: C <:< TempIdCommand[Tag]) {
      def forIt[T](factory: UUID @@ Tag => T): T =
        factory(command.tempId)

      def andForIt[T](factory: UUID @@ Tag => T): C :: T :: HNil =
        command :: factory(command.tempId) :: HNil

      def andForItAll[T <: HList](commands: UUID @@ Tag => T): C :: T =
        command :: commands(command.tempId)
    }
  }
}
