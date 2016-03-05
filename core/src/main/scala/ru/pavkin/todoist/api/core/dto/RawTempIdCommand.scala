package ru.pavkin.todoist.api.core.dto

import java.util.UUID

case class RawTempIdCommand[A](`type`: String, uuid: UUID, args: A, temp_id: UUID)
