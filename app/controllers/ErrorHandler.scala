package controllers

import play.api.Logger
import play.api.http.HttpErrorHandler
import play.api.libs.json.Json
import play.api.mvc.Results._
import play.api.mvc._

import scala.concurrent._

class ErrorHandler extends HttpErrorHandler {

  val logger = Logger(classOf[ErrorHandler])

  def onClientError(request: RequestHeader, statusCode: Int, message: String) = {
    Future.successful {
      logger.warn(s"$statusCode error occurred at ${request.uri} - $message")
      Status(statusCode)(Json.obj("message" -> message))
    }
  }

  def onServerError(request: RequestHeader, exception: Throwable) = {
    Future.successful {
      logger.error(s"server error occurred at ${request.uri}", exception)
      InternalServerError(Json.obj("message" -> exception.getMessage))
    }
  }
}