package models.auth

import models.ui.{Errors, InvalidParameter, ParamError}
import play.api.libs.json.{JsError, JsSuccess, JsValue, Reads}

import scalaz.{-\/, \/, \/-}

case class AuthRequest(username: String, rawpassword: String) {

}

object AuthRequest {
  def from(json: JsValue)(implicit ev: Reads[AuthRequest]): Errors\/AuthRequest = {
    json.validate[AuthRequest] match {
      case JsSuccess(ar, _) => \/-(ar)
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
