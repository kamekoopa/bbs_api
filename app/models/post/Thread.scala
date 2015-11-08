package models.post

import java.util.Date

import models.user.User

case class Thread(id: Int, title: String, createdBy: User, createdAt: Date, lastPostedAt: Option[Date], tags: Seq[Tag]) {

}
