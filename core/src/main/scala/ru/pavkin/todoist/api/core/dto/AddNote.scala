package ru.pavkin.todoist.api.core.dto

import ru.pavkin.todoist.api.core.IsResourceId

case class AddNote[T: IsResourceId](content: String,
                                    item_id: T,
                                    uids_to_notify: List[Int] = Nil)
