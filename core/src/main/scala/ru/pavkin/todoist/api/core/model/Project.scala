package ru.pavkin.todoist.api.core.model

import java.util.Date

sealed trait Project {
  def id: ProjectId
  def userId: UserId
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

case class RegularProject(id: ProjectId,
                          userId: UserId,
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

case class ArchivedProject(id: ProjectId,
                           userId: UserId,
                           name: String,
                           color: ProjectColor,
                           indent: Indent,
                           order: Int,
                           isCollapsed: Boolean,
                           isShared: Boolean,
                           isDeleted: Boolean,
                           archivedAt: Date) extends Project {
  val isArchived: Boolean = true
  val isInbox: Boolean = false
  val isTeamInbox: Boolean = false
}


