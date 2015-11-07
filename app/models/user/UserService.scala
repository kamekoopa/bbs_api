package models.user

import models.ui.Errors

import scalaz.\/

class UserService {

  val userRepo: UserRepository = new UserRepositoryOnRDB

  def findUserById(id: Int): Errors\/User = {
    userRepo.findById(id)
  }
}
