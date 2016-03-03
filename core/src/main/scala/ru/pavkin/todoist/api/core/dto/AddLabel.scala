package ru.pavkin.todoist.api.core.dto

case class AddLabel(name: String,
                    color: Option[Int] = None,
                    item_order: Option[Int] = None)
