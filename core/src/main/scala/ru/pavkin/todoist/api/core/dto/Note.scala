package ru.pavkin.todoist.api.core.dto

case class FileAttachment(file_name: String,
                          file_size: Long,
                          file_type: String,
                          file_url: String,
                          upload_state: Option[String])

case class Note(id: Int,
                posted_uid: Int,
                item_id: Int,
                project_id: Int,
                content: String,
                file_attachment: Option[FileAttachment],
                uids_to_notify: Option[List[Int]],
                is_deleted: Int,
                is_archived: Int,
                posted: String)
