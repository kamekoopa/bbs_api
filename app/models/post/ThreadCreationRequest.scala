package models.post

import models.auth.AccessToken
import models.ui.{Errors, InvalidParameter, ParamError}
import play.api.libs.json.{JsError, JsSuccess, JsValue, Reads}

import scalaz.{-\/, \/, \/-}

case class ThreadCreationRequest(title: String, tagNames: Seq[String], token: String) {

  val accessToken = new AccessToken(token)
}

object ThreadCreationRequest {
  def from(json: JsValue)(implicit ev: Reads[ThreadCreationRequest]): Errors\/ThreadCreationRequest = {
    json.validate[ThreadCreationRequest] match {
      case JsSuccess(tcr, _) => \/-(tcr)
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
