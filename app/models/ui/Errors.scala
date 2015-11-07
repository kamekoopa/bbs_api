package models.ui

import play.api.libs.json.Writes
import play.api.libs.json.Json._
import play.api.mvc.Result
import play.api.mvc.Results._
import play.api.mvc.Results.{InternalServerError => IS}

sealed trait Errors {
  def toResult(implicit ev: Writes[Errors]): Result = {
    this match {
      case e: ResourceNotFound => NotFound(toJson(e))
      case e: InternalServerError => IS(toJson(e))
    }
  }
}
final case class ResourceNotFound(message: String) extends Errors
final case class InternalServerError(message: String) extends Errors