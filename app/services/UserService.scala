package services

import models.ui.Errors
import models.user.{User, UserCreationRequest, UserRepository, UserRepositoryOnRDB}

import scalaz.\/

class UserService(val userRepo: UserRepository) {

  def findUserById(id: Int): Errors\/User = {
    userRepo.findById(id)
  }

  def createUser(ucr: UserCreationRequest): Errors\/User = {
    userRepo.createUser(ucr)
  }
}
