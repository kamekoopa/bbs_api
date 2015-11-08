package models.user

import models.auth.Password
import models.ui.{Errors, InternalServerError, ResourceNotFound}
import scalikejdbc._

import scala.util.{Failure, Success, Try}
import scalaz._

class UserRepositoryOnRDB extends UserRepository {

  def findById(id: Int)(implicit sess: DBSession = AutoSession): Errors\/User = {

    selectById(id) match {
      case Failure(e)          => -\/(InternalServerError(e.getMessage))
      case Success(None)       => -\/(ResourceNotFound(s"specified id: $id was not found"))
      case Success(Some(user)) => \/-(user)
    }
  }

  def findByName(name: String)(implicit sess: DBSession = AutoSession): Errors\/User = {

    selectByName(name) match {
      case Failure(e)          => -\/(InternalServerError(e.getMessage))
      case Success(None)       => -\/(ResourceNotFound(s"specified id: $name was not found"))
      case Success(Some(user)) => \/-(user)
    }
  }

  def createUser(ucr: UserCreationRequest)(implicit sess: DBSession = AutoSession): Errors\/User = {

    val createdUser = for {
      genId <- insertUser(ucr)
      user <- selectById(genId)
    } yield user

    createdUser match {
      case Failure(e)          => -\/(InternalServerError(e.getMessage))
      case Success(None)       => -\/(InternalServerError("user creation failed"))
      case Success(Some(user)) => \/-(user)
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

  private def insertUser(ucr: UserCreationRequest)(implicit sess:DBSession): Try[Int] = {
    Try {
      sql"""insert into users(username, password, email, salt)
             values (${ucr.username}, ${ucr.password.hashedPassword}, ${ucr.email}, ${ucr.password.salt})"""
        .updateAndReturnGeneratedKey().apply().toInt
    }
  }
}
