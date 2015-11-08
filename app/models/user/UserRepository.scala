package models.user

import models.ui.Errors

import scalaz._

trait UserRepository {

  def findById(id: Int): Errors\/User

  def findByName(name: String): Errors\/User

  def createUser(ucr: UserCreationRequest): Errors\/User
}
