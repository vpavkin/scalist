package ru.pavkin.todoist.api.core.model.util

import java.util.UUID

import ru.pavkin.todoist.api.core.model.{TempIdCommand, Command}
import shapeless.tag.@@
import shapeless.{HList, ::, HNil}

object CombineCommands {
  trait Syntax {
    implicit class CombineCommandsOps[C <: Command](command: C) {
      /**
        * Combines two commands into an `HList`
        *
        * @example {{{command1 :+ command2}}}
        */
      def :+[A <: Command](other: A): C :: A :: HNil = command :: other :: HNil
    }

    implicit class TempIdProduceCommandsOps[Tag, C](command: C)(implicit ev: C <:< TempIdCommand[Tag]) {
      /**
        * Creates a dependant command that uses the `tempId` of this command.
        * Returns '''only''' the dependant command.
        *
        * @param factory A function that receives a tempId and creates a command.
        * @return The dependant command '''only'''
        * @example {{{AddProject("p").forIt(AddTask("t",_) // AddTask}}}
        */
      def forIt[T](factory: UUID @@ Tag => T): T =
        factory(command.tempId)

      /**
        * Creates a dependant command that uses the `tempId` of this command '''and
        * stacks it to this command'''.
        * Returns an `HList` of this command and the dependant command.
        *
        * @param factory A function that receives a tempId and creates a command.
        * @return An `HList` of this command and the dependant command.
        * @example {{{AddProject("p").andForIt(AddTask("t",_) // AddProject :: AddTask :: HNil}}}
        */
      def andForIt[T](factory: UUID @@ Tag => T): C :: T :: HNil =
        command :: factory(command.tempId) :: HNil

      /**
        * Creates multiple dependant commands that use the `tempId` of this command '''and
        * stacks them to this command'''.
        * Returns an `HList` of this command and the dependant commands.
        *
        * @param commands A function that receives a tempId and creates an `HList` of commands.
        * @return An `HList` of this command and the dependant commands.
        * @example {{{
        *           AddProject("p").andForItAll(id => AddTask("t1",id) :+ AddTask("t2",id))
        *           // AddProject :: AddTask :: AddTask :: HNil
        *          }}}
        */
      def andForItAll[T <: HList](commands: UUID @@ Tag => T): C :: T =
        command :: commands(command.tempId)
    }
  }
}
