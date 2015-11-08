package models.auth

case class Password(hashedPassword: String, salt: String) {

}
object Password {
  def create(rawPassword: String, salt: String): Password = Password(rawPassword, salt)
}