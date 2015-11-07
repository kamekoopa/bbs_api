package models.user

import models.auth.Password
import models.ui.{ResourceConflict, InternalServerError, Errors, ResourceNotFound}
import scalikejdbc._
import TxBoundary.Try._
import scala.util.Try
import scala.util.Failure
import scala.util.Success
import scalaz._

class UserRepositoryOnRDB extends UserRepository {

  def findById(id: Int): Errors\/User = {

    val userTryOpt = DB localTx { implicit sess =>
      selectById(id)
    }

    userTryOpt match {
      case Failure(e)          => -\/(InternalServerError(e.getMessage))
      case Success(None)       => -\/(ResourceNotFound(s"specified id: $id was not found"))
      case Success(Some(user)) => \/-(user)
    }
  }

  def createUser(ucr: UserCreationRequest): Errors\/User = {

    DB localTx { implicit sess =>

      val existsV: Errors\/Unit = selectByName(ucr.username) match {
        case Failure(e)          => -\/(InternalServerError(e.getMessage))
        case Success(Some(user)) => -\/(ResourceConflict(s"specified username ${user.username} is already exists"))
        case Success(None)       => \/-(())
      }

      existsV.flatMap({ unit =>
        insertUser(ucr) match {
          case Failure (e)     => -\/ (InternalServerError (e.getMessage))
          case Success (genId) => \/-(genId)
        }
      }).flatMap({ genId =>
        selectById(genId) match {
          case Failure(e)          => -\/(InternalServerError(e.getMessage))
          case Success(None)       => -\/(InternalServerError("user creation failed"))
          case Success(Some(user)) => \/-(user)
        }
      })
    }
  }

  private def selectById(id: Int)(implicit sess: DBSession): Try[Option[User]] = {
    Try {
      sql"""select id, username, email, password, salt from users where id = $id""".map({rs =>
        User(
          rs.int("id"),
          rs.string("username"),
          rs.string("email"),
          Password(rs.string("password"), rs.string("salt"))
        )
      }).single().apply()
    }
  }

  private def selectByName(username: String)(implicit sess: DBSession): Try[Option[User]] = {
    Try {
      sql"""select id, username, email, password, salt from users where username = $username""".map({rs =>
        User(
          rs.int("id"),
          rs.string("username"),
          rs.string("email"),
          Password(rs.string("password"), rs.string("salt"))
        )
      }).single().apply()
    }
  }

  private def existsName(username: String)(implicit sess: DBSession): Try[Boolean] = {
    Try {
      val userOpt = sql"""select id, username, email, password, salt from users where username = $username""".map({rs =>
        User(
          rs.int("id"),
          rs.string("username"),
          rs.string("email"),
          Password(rs.string("password"), rs.string("salt"))
        )
      }).single().apply()

      userOpt.isDefined
    }
  }

  private def insertUser(ucr: UserCreationRequest)(implicit sess:DBSession): Try[Int] = {
    Try {
      sql"""insert into users(username, password, email, salt)
             values (${ucr.username}, ${ucr.password.hashedPassword}, ${ucr.email}, ${ucr.password.salt})"""
        .updateAndReturnGeneratedKey().apply().toInt
    }
  }
}
