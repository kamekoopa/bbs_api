package infra.json

import models.ui.{Errors, InvalidParameter, ParamError}
import play.api.libs.json.{JsError, JsSuccess, JsValue, Reads}

import scalaz.{-\/, \/, \/-}

object DeSerializeUtils {

  def from[T](json: JsValue)(implicit ev: Reads[T]): Errors\/T = {
    json.validate[T] match {
      case JsSuccess(value, _) => \/-(value)
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
