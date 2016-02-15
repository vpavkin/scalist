package ru.pavkin.todoist.api.core.dto

case class Label(id: Int,
                 uid: Int,
                 name: String,
                 color: Int,
                 item_order: Int,
                 is_deleted: Int)
