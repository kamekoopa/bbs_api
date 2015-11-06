package models.user

import models.ui.ResourceNotFound

import scalaz._

trait UserRepository {

  def findById(id: Int): ResourceNotFound\/User
}
