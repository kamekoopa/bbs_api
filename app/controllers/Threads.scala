package controllers

import javax.inject.Inject

import infra.json.DeSerializeUtils
import infra.json.ReadsWrites._
import models.post.{ThreadCreationRequest, ThreadRepositoryOnRDB}
import models.user.UserRepositoryOnRDB
import play.api.Configuration
import play.api.cache.CacheApi
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json.Json._
import play.api.mvc.{Action, Controller}
import services.{AuthService, ThreadService}

import scala.concurrent.Future

class Threads @Inject() (cacheApi: CacheApi, config: Configuration) extends Controller with Logging {

  val authService = new AuthService(new UserRepositoryOnRDB, cacheApi, config)
  val threadService = new ThreadService(new ThreadRepositoryOnRDB)

  def createThread = Action.async(parse.json) { req =>
    Future {
      withRequestLogging(req){ req =>
        val threadV = for {
          tcr <- DeSerializeUtils.from[ThreadCreationRequest](req.body)
          user <- authService.getAuthorizedUser(tcr.accessToken)
          thread <- threadService.createNewThread(tcr.title, user, tcr.tagNames)
        } yield thread

        threadV.fold(_.toResult,  thread => Created(toJson(thread)))
      }
    }
  }
}
