package models.ui

import play.api.libs.json.Json._
import play.api.libs.json.Writes
import play.api.mvc.Result
import play.api.mvc.Results.{InternalServerError => IS, _}

sealed trait Errors {
  def toResult(implicit ev: Writes[Errors]): Result = {
    this match {
      case e: InvalidParameter => BadRequest(toJson(e))
      case e: UnAuthError => Unauthorized(toJson(e))
      case e: ResourceNotFound => NotFound(toJson(e))
      case e: ResourceConflict => Conflict(toJson(e))
      case e: InternalServerError => IS(toJson(e))
    }
  }
}
final case class InvalidParameter(errors: Seq[ParamError]) extends Errors
final case class ParamError(paramName: String, message: String)
final case class UnAuthError(message: String) extends Errors
final case class ResourceNotFound(message: String) extends Errors
final case class ResourceConflict(message: String) extends Errors
final case class InternalServerError(message: String) extends Errors