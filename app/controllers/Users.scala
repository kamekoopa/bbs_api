package controllers

import models.user.{UserRepositoryOnRDB, UserCreationRequest}
import infra.json.ReadsWrites._
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json.Json._
import play.api.mvc.{Action, Controller}
import services.UserService

import scala.concurrent.Future

class Users extends Controller {

  val userService = new UserService(new UserRepositoryOnRDB)

  def findById(id: Int) = Action.async {

    Future {
      val userV = userService.findUserById(id)
      userV.fold(_.toResult,  user => Ok(toJson(user)))
    }
  }

  def createUser = Action.async(parse.json) { req =>
    Future {
      val userV = for {
        ucr <- UserCreationRequest.from(req.body)
        user <- userService.createUser(ucr)
      } yield user

      userV.fold(_.toResult,  user => Accepted(toJson(user)))
    }
  }
}
