package infra.json

import models.ui.{InternalServerError, ResourceNotFound, Errors}
import models.user.User
import play.api.libs.json._

object ReadsWrites {

  implicit val _user = new OWrites[User] {
    def writes(o: User): JsObject = {
      Json.obj(
        "id" -> o.id,
        "username" -> o.username,
        "email" -> o.email
      )
    }
  }

  implicit val _error = new OWrites[Errors] {
    def writes(o: Errors): JsObject = {
      o match {
        case e: ResourceNotFound =>
          Json.obj(
            "message" -> e.message
          )
        case e: InternalServerError =>
          Json.obj(
            "message" -> e.message
          )
      }
    }
  }
}
