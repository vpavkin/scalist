package ru.pavkin.todoist.api.core.model

import java.util.UUID

import org.scalatest.prop.Checkers
import org.scalatest.{FunSuite, Matchers}
import ru.pavkin.todoist.api.core.model.util.{ReversedAtSyntax, CommandResultHList, CombineCommands}
import shapeless.HNil
import shapeless.test.{typed, illTyped}

class CommandsResultsSyntaxSpec extends FunSuite
  with Matchers
  with Checkers
  with CommandResultHList.Syntax
  with ReversedAtSyntax {

  def id = UUID.randomUUID()

  val success = CommandResult(id, CommandSuccess)
  val failure = CommandResult(id, CommandFailure(1, "error"))
  val tempIdSuccess = TempIdCommandResult(id, TempIdSuccess(id, 666))
  val tempIdFailure = TempIdCommandResult(id, TempIdFailure(2, "error"))
  val multiSuccess = CommandResult(id, MultiItemCommandStatus(Map(1 -> CommandSuccess, 2 -> CommandSuccess)))
  val partialFailure = CommandResult(
    id, MultiItemCommandStatus(Map(1 -> CommandFailure(1, "error"), 2 -> CommandSuccess))
  )

  test("Typesafe resultFor should get the command under reversed index") {
    val result = success :: tempIdFailure :: HNil
    result.resultFor(_0) shouldBe tempIdFailure
    typed[TempIdCommandResult](result.resultFor(_0))
    result.resultFor(_1) shouldBe success
    typed[CommandResult](result.resultFor(_1))
    illTyped("""result.resultFor(_2)""")
  }

  test("Runtime resultFor should get the command with uuid") {
    val result = success :: tempIdFailure :: HNil
    result.resultFor(success.uuid) shouldBe Some(success)
    typed[Option[TodoistCommandResult]](result.resultFor(success.uuid))
    result.resultFor(tempIdFailure.uuid) shouldBe Some(tempIdFailure)
    result.resultFor(failure.uuid) shouldBe None
  }

  test("single command result isSuccess is true only in case of success") {
    success.isSuccess shouldBe true
    tempIdSuccess.isSuccess shouldBe true
    multiSuccess.isSuccess shouldBe true
    failure.isSuccess shouldBe false
    tempIdFailure.isSuccess shouldBe false
    partialFailure.isSuccess shouldBe false
  }

  test("Multiple results are success if all commands succeed") {
    (success :: tempIdSuccess :: multiSuccess :: HNil).isSuccess shouldBe true
    (success :: tempIdSuccess :: failure :: HNil).isSuccess shouldBe false
    (partialFailure :: success :: HNil).isSuccess shouldBe false
  }
}

