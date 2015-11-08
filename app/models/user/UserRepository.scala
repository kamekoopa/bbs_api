package models.user

import models.ui.Errors
import scalikejdbc.{AutoSession, DBSession}

import scalaz._

trait UserRepository {

  def findById(id: Int)(implicit sess: DBSession = AutoSession): Errors\/User

  def findByName(name: String)(implicit sess: DBSession = AutoSession): Errors\/User

  def createUser(ucr: UserCreationRequest)(implicit sess: DBSession = AutoSession): Errors\/User
}
