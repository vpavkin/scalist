package ru.pavkin.todoist.api.circe.dto

import io.circe.{Printer, Json, Encoder}
import io.circe.syntax._
import ru.pavkin.todoist.api.circe.encoders.CirceDTOEncoders
import ru.pavkin.todoist.api.core.ToRawRequest
import ru.pavkin.todoist.api.core.dto.{RawTempIdCommand, RawCommand}

trait CirceDTOCommands extends CirceDTOEncoders {

  private def print(json: Json) = Printer.noSpaces.copy(dropNullKeys = true).pretty(json)

  implicit def commandIsRawRequest1[A: Encoder]: ToRawRequest[RawCommand[A]] = ToRawRequest.command(
    c => List(print(c.asJson))
  )
  implicit def commandIsRawRequest2[A: Encoder]: ToRawRequest[RawTempIdCommand[A]] = ToRawRequest.command(
    c => List(print(c.asJson))
  )
}
