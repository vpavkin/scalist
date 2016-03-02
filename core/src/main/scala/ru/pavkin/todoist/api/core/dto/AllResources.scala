package ru.pavkin.todoist.api.core.dto

case class AllResources(Projects: Option[List[Project]],
                        Labels: Option[List[Label]],
                        Items: Option[List[Task]],
                        Notes: Option[List[Note]],
                        Filters: Option[List[Filter]],
                        Reminders: Option[List[Reminder]],
                        User: Option[User])


