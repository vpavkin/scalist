package ru.pavkin.todoist.api.core.dto

case class AllResources(Projects: Option[List[Project]],
                        Labels: Option[List[Label]],
                        Items: Option[List[Task]])


