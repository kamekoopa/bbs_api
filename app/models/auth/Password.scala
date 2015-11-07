package models.auth

case class Password(hashedPassword: String, salt: String) {

}
