package models.auth

case class Password(hashedPassword: String) {

}

object Password {
  def apply(rawPasswordString: String, salt: String): Password = {
    new Password("test")
  }
}
