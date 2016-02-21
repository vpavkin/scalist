package ru.pavkin.todoist.api.core.model

import ru.pavkin.todoist.api.core.tags
import shapeless.tag.@@

sealed trait Project {
  def id: Int @@ tags.ProjectId
  def userId: Int @@ tags.UserId
  def name: String
  def color: ProjectColor
  def indent: Indent
  def order: Int
  def isCollapsed: Boolean
  def isShared: Boolean
  def isDeleted: Boolean
  def isArchived: Boolean
  def isInbox: Boolean
  def isTeamInbox: Boolean
}

case class RegularProject(id: Int @@ tags.ProjectId,
                          userId: Int @@ tags.UserId,
                          name: String,
                          color: ProjectColor,
                          indent: Indent,
                          order: Int,
                          isCollapsed: Boolean,
                          isShared: Boolean,
                          isDeleted: Boolean,
                          isInbox: Boolean,
                          isTeamInbox: Boolean) extends Project {
  val isArchived: Boolean = false
}


