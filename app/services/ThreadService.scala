package services

import infra.db.TxBoundaries._
import models.post.{Thread => Th, ThreadRepository}
import models.ui.Errors
import models.user.User
import scalikejdbc._

import scalaz.\/

class ThreadService(val threadRepo: ThreadRepository) {

  def createNewThread(threadTitle: String, autor: User, tagNames: Seq[String]): Errors\/Th = {

    DB localTx { implicit sess =>

      for {
        tags <- threadRepo.createTag(tagNames:_*)
        newThreadId <- threadRepo.createThread(threadTitle, autor)
        newThread <- threadRepo.findThreadById(newThreadId)
        registered <- threadRepo.insertThreadTagsRelation(newThread, tags)
      } yield registered
    }
  }
}
