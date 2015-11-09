package models.user

import models.auth.Password

case class UserCreationRequest(username: String, email: String, password: Password)

object UserCreationRequest {

  def create(username: String, email: String, rawPassword: String): UserCreationRequest = {
    UserCreationRequest(username, email, Password.create(rawPassword, email))
  }
}

case class User(id: Int, username: String, email: String, password: Password) {

}
