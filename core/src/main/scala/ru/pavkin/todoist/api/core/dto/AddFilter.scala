package ru.pavkin.todoist.api.core.dto

case class AddFilter(name: String,
                     query: String,
                     color: Int,
                     order: Option[Int] = None)


