package models.post

import models.ui.Errors
import models.user.User
import scalikejdbc.{AutoSession, DBSession}

import scalaz.\/

trait ThreadRepository {

  def createThread(threadTitle: String, author: User)(implicit sess: DBSession = AutoSession): Errors\/Int

  def findThreadById(id: Int)(implicit sess: DBSession = AutoSession): Errors\/Thread

  def createTag(tagNames: String*)(implicit sess: DBSession = AutoSession): Errors\/Seq[Tag]

  def findTagByName(name: String)(implicit sess: DBSession = AutoSession): Errors\/Tag

  def insertThreadTagsRelation(thread: Thread, tags: Seq[Tag])(implicit sess: DBSession = AutoSession): Errors\/Thread
}
