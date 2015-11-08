package controllers

import javax.inject.Inject

import infra.json.ReadsWrites._
import models.auth.{AuthRequest, AuthService}
import play.api.Configuration
import play.api.cache.CacheApi
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json.Json
import play.api.libs.json.Json._
import play.api.mvc.{Action, Controller}

import scala.concurrent.Future

class Authentication @Inject() (cacheApi: CacheApi, config: Configuration) extends Controller {

  val authService = new AuthService(cacheApi, config)

  def authenticate = Action.async(parse.json) { req =>
    Future {
      val tokenV = for {
        authReq <- AuthRequest.from(req.body)
        token <- authService.authenticate(authReq)
      } yield token

      tokenV.fold(_.toResult,  token => Created(Json.obj("token" -> token.token)))
    }
  }
}
