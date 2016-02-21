package ru.pavkin.todoist.api.core.model.util

import java.util.UUID

import ru.pavkin.todoist.api.core.model.TodoistCommandResult
import shapeless.{HNil, HList, ::}

trait CommandResultHList[T <: HList] {
  def isSuccess(result: T): Boolean
  def resultFor(result: T)(uuid: UUID): Option[TodoistCommandResult]
}

object CommandResultHList extends CommandResultHListSyntax {

  implicit val hnil: CommandResultHList[HNil] = new CommandResultHList[HNil] {
    def resultFor(result: HNil)(uuid: UUID): Option[TodoistCommandResult] = None
    def isSuccess(result: HNil): Boolean = true
  }

  implicit def hlist[H, T <: HList](implicit
                                    R: CommandResultHList[T],
                                    C: H <:< TodoistCommandResult): CommandResultHList[H :: T] =
    new CommandResultHList[H :: T] {
      def resultFor(result: H :: T)(uuid: UUID): Option[TodoistCommandResult] = {
        val hcomm = C(result.head)
        if (hcomm.uuid == uuid)
          Some(hcomm)
        else
          result.tail.resultFor(uuid)
      }
      def isSuccess(result: H :: T): Boolean = C(result.head).isSuccess && result.tail.isSuccess
    }
}

trait CommandResultHListSyntax {
  implicit class CommandResultHListOps[A <: HList](a: A)(implicit ev: CommandResultHList[A]) {
    def resultFor(uuid: UUID): Option[TodoistCommandResult] = ev.resultFor(a)(uuid)
    def isSuccess: Boolean = ev.isSuccess(a)
  }
}
