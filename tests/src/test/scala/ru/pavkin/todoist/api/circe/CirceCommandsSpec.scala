package ru.pavkin.todoist.api.circe

import java.util.UUID

import org.scalatest.{FunSuite, Matchers}
import ru.pavkin.todoist.api.circe.dto.CirceDTOCommands
import ru.pavkin.todoist.api.core.ToRawRequest
import ru.pavkin.todoist.api.core.dto.{AddProject, RawTempIdCommand, RawCommand}

class CirceCommandsSpec extends FunSuite with Matchers with CirceDTOCommands {

  import ToRawRequest.syntax._

  test("RawCommand encodes to minified json") {
    val uuid = UUID.randomUUID
    RawCommand("type", uuid, "str").toRawRequest shouldBe
      Map("commands" -> List(s"""{"type":"type","uuid":"$uuid","args":"str"}"""))
  }

  test("RawTempIdCommand encodes to minified json") {
    val uuid = UUID.randomUUID
    val tempId = UUID.randomUUID
    RawTempIdCommand("type", uuid, "str", tempId).toRawRequest shouldBe
      Map("commands" -> List(s"""{"type":"type","uuid":"$uuid","args":"str","temp_id":"$tempId"}"""))
  }

  test("None fields doesn't encode") {
    val uuid = UUID.randomUUID
    RawCommand("type", uuid, AddProject("project")).toRawRequest shouldBe
      Map("commands" -> List(s"""{"type":"type","uuid":"$uuid","args":{"name":"project"}}"""))
  }
}


