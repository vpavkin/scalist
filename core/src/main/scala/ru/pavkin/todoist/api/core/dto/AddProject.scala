package ru.pavkin.todoist.api.core.dto

case class AddProject(name: String,
                      color: Option[Int] = None,
                      indent: Option[Int] = None,
                      item_order: Option[Int] = None)
