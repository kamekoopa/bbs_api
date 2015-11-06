package controllers

import models.user.UserRepositoryOnRDB
import play.api.libs.concurrent.Execution.Implicits._
import play.api.mvc.{Action, Controller}
import play.api.http.Writeable._

import scala.concurrent.Future

class Users extends Controller {

  val userRepo = new UserRepositoryOnRDB()

  def findById(id: Int) = Action.async {

    Future {
      userRepo.findById(id).fold(err => NotFound(err.message), user => Ok(user.username))
    }
  }
}
