package models.user

import models.auth.Password

case class User(id: Int, username: String, email: String, password: Password) {

}
