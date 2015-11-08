package infra.json

import models.auth.AuthRequest
import models.post.{Tag, ThreadCreationRequest}
import models.ui._
import models.user.{User, UserCreationRequest}
import play.api.libs.functional.syntax._
import play.api.libs.json.Json._
import play.api.libs.json.Reads._
import play.api.libs.json._
import play.jsonext.{CaseClassReads, CaseClassWrites}

object ReadsWrites {

  implicit val _authRequest = (
    (JsPath \ "username").read[String] and
    (JsPath \ "password").read[String]
  )(AuthRequest.apply _)

  implicit val _userCreationReq = (
    (JsPath \ "username").read[String](maxLength[String](10)) and
    (JsPath \ "email").read[String] and
    (JsPath \ "password").read[String](minLength[String](10))
  )(UserCreationRequest.create _)

  implicit val _user = new OWrites[User] {
    def writes(o: User): JsObject = {
      Json.obj(
        "id" -> o.id,
        "username" -> o.username,
        "email" -> o.email
      )
    }
  }

  implicit val _threadCreationRequest = CaseClassReads(ThreadCreationRequest.apply _)(
    "title", "tagNames", "token"
  )

  implicit val _tag = CaseClassWrites(Tag.unapply _)(
    "id", "name"
  )

  implicit val _thread = CaseClassWrites(models.post.Thread.unapply _)(
    "id", "title", "createdBy", "createdAt", "lastPostedAt", "tags"
  )

  implicit val _paramError = CaseClassWrites(ParamError.unapply _)(
    "paramName", "message"
  )

  implicit val _error = new OWrites[Errors] {
    def writes(o: Errors): JsObject = {
      o match {
        case e: InvalidParameter =>
          Json.obj(
            "errors" -> toJson(e.errors)
          )
        case e: UnAuthError =>
          Json.obj(
            "message" -> e.message
          )
        case e: ResourceNotFound =>
          Json.obj(
            "message" -> e.message
          )
        case e: ResourceConflict =>
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
