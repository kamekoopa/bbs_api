package models.user

import models.auth.Password
import models.ui.{InternalServerError, Errors, ResourceNotFound}
import scalikejdbc._
import TxBoundary.Try._
import scala.util.Try
import scala.util.Failure
import scala.util.Success
import scalaz._

class UserRepositoryOnRDB extends UserRepository {

  def findById(id: Int): Errors\/User = {

    val userTryOpt = DB localTx { implicit sess =>
      Try {
        sql"""select id, username, email, password from users where id = $id""".map({rs =>
          User(
            rs.int("id"),
            rs.string("username"),
            rs.string("email"),
            Password(rs.string("password"))
          )
        }).single().apply()
      }
    }

    userTryOpt match {
      case Failure(e)          => -\/(InternalServerError(e.getMessage))
      case Success(None)       => -\/(ResourceNotFound(s"specified id: $id was not found"))
      case Success(Some(user)) => \/-(user)
    }
  }
}
