package services

import infra.db.TxBoundaries._
import models.ui.{Errors, ResourceConflict, ResourceNotFound}
import models.user.{User, UserCreationRequest, UserRepository}
import scalikejdbc._

import scalaz.{-\/, \/, \/-}

class UserService(val userRepo: UserRepository) {

  def findUserById(id: Int): Errors\/User = {
    DB localTx { implicit sess =>
      userRepo.findById(id)
    }
  }

  def createUser(ucr: UserCreationRequest): Errors\/User = {

    DB localTx { implicit sess =>

      userRepo.findByName(ucr.username) match {
        case -\/(ResourceNotFound(_)) => userRepo.createUser(ucr)
        case v @ -\/(_)               => v
        case \/-(user)                => -\/(ResourceConflict(s"specified username ${user.username} is already exists"))
      }
    }
  }
}
