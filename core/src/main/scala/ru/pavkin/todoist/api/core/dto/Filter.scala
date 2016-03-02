package ru.pavkin.todoist.api.core.dto

case class Filter(id: Int,
                  name: String,
                  query: String,
                  color: Int,
                  item_order: Int,
                  is_deleted: Int)
