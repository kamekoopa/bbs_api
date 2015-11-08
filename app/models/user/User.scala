package models.user

import models.auth.Password
import models.ui.{Errors, InvalidParameter, ParamError}
import play.api.libs.json.{JsError, JsSuccess, JsValue, Reads}

import scalaz.{-\/, \/, \/-}

case class UserCreationRequest(username: String, email: String, password: Password)

object UserCreationRequest {

  def create(username: String, email: String, rawPassword: String): UserCreationRequest = {
    UserCreationRequest(username, email, Password.create(rawPassword, email))
  }

  def from(json: JsValue)(implicit ev: Reads[UserCreationRequest]): Errors\/UserCreationRequest = {
    json.validate[UserCreationRequest] match {
      case JsSuccess(ucr, _) => \/-(ucr)
      case JsError(errors) =>

        val paramErrors = for {
          (path, verrors) <- errors
          verror <- verrors
        } yield {
          ParamError(path.toString(), verror.message)
        }

        -\/(InvalidParameter(paramErrors))
    }
  }
}

case class User(id: Int, username: String, email: String, password: Password) {

}
