package models.user

import models.auth.Password
import models.ui.ResourceNotFound
import scalikejdbc._
import scalaz._

class UserRepositoryOnRDB extends UserRepository {

  def findById(id: Int): ResourceNotFound\/User = {

    DB readOnly { implicit sess =>

      val userOpt = sql"""select id, username, email, password from users where id = $id""".map({rs =>
        User(
          rs.int("id"),
          rs.string("username"),
          rs.string("email"),
          Password(rs.string("password"))
        )
      }).single().apply()

      userOpt.fold[ResourceNotFound\/User](-\/(ResourceNotFound(s"specified id: $id was not found")))(\/-(_))
    }
  }
}
