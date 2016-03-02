package ru.pavkin.todoist.api.core.model

import ru.pavkin.todoist.api.core.tags
import shapeless.tag.@@

case class Filter(id: Int @@ tags.FilterId,
                  name: String,
                  query: String,
                  color: LabelColor,
                  order: Int,
                  isDeleted: Boolean)
