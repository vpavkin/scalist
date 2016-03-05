package ru.pavkin.todoist.api.core.dto

import java.util.UUID

case class RawCommand[A](`type`: String, uuid: UUID, args: A)
