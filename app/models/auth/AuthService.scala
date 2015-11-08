package models.auth

import java.util.Date
import java.util.concurrent.TimeUnit

import models.ui.{Errors, ResourceNotFound, UnAuthError}
import models.user.{UserRepository, UserRepositoryOnRDB}
import play.api.Configuration
import play.api.cache.CacheApi

import scala.concurrent.duration.Duration
import scalaz.{-\/, \/, \/-}

class AuthService(val cacheApi: CacheApi, val config: Configuration) {

  val userRepo: UserRepository = new UserRepositoryOnRDB

  def authenticate(authReq: AuthRequest): Errors\/AccessToken = {

    val user = for {
      user <- userRepo.findByName(authReq.username)
    } yield user

    lazy val authError = UnAuthError("invalid username or password")

    user
      .leftMap({
        case ResourceNotFound(_) => authError
        case other => other
      })
      .flatMap({user =>
        val specifiedPassword = Password.create(authReq.rawpassword, user.email)
        if (user.password == specifiedPassword) {

          val expiration = Duration(
            config.getMilliseconds("app.authExpiration").getOrElse(TimeUnit.HOURS.toMillis(24L)),
            TimeUnit.MILLISECONDS
          )
          val token = new Date().getTime.toString

          cacheApi.set(s"auth.${user.username}", token, expiration)
          \/-(new AccessToken(token))

        } else {
          -\/(authError)
        }
      })
  }
}
