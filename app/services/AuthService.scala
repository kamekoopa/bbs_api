package services

import java.util.Date
import java.util.concurrent.TimeUnit

import infra.db.TxBoundaries._
import models.auth.{AccessToken, AuthRequest, Password}
import models.ui.{Errors, ResourceNotFound, UnAuthError}
import models.user.{User, UserRepository}
import play.api.Configuration
import play.api.cache.CacheApi
import scalikejdbc._

import scala.concurrent.duration.Duration
import scalaz.{-\/, \/, \/-}

class AuthService(val userRepo: UserRepository, val cacheApi: CacheApi, val config: Configuration) {

  def authenticate(authReq: AuthRequest): Errors\/AccessToken = {

    lazy val authError = UnAuthError("invalid username or password")

    DB.localTx { implicit sess =>

      val user = for {
        user <- userRepo.findByName(authReq.username)
      } yield user

      user
        .leftMap({
          case ResourceNotFound(_) => authError
          case other => other
        })
        .flatMap({ user =>

          val specifiedPassword = Password.create(authReq.rawpassword, user.email)

          if (user.password == specifiedPassword) {
            \/-(setToken(user))
          } else {
            -\/(authError)
          }
        })
    }
  }

  def getAuthorizedUser(token: AccessToken): Errors\/User = {

    lazy val unAuth = -\/(UnAuthError("unauthorized"))

    cacheApi
      .get[Int](s"auth.${token.token}")
      .fold[Errors\/User](unAuth){ userId =>

        userRepo.findById(userId) match {
          case -\/(ResourceNotFound(_)) => unAuth
          case l @ -\/(_)               => l
          case r @ \/-(_)               => r
        }
      }
  }

  private def setToken(user: User): AccessToken = {

    val expiration = Duration(
      config.getMilliseconds("app.authExpiration").getOrElse(TimeUnit.HOURS.toMillis(24L)),
      TimeUnit.MILLISECONDS
    )
    val token = new AccessToken(new Date().getTime.toString)

    cacheApi.set(s"auth.${token.token}", user.id, expiration)

    token
  }
}
