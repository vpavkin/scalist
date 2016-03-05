package ru.pavkin.todoist.api.core.dto

case class MoveTasks(project_items: Map[String, List[Int]], to_project: Int)
