import org.junit.runner._
import org.specs2.execute.{AsResult, Result}
import org.specs2.mutable._
import org.specs2.runner._
import play.api.Application
import play.api.libs.json.Json
import play.api.libs.json.Reads._
import play.api.test.Helpers._
import play.api.test._
import scalikejdbc._

import scala.concurrent.Await
import scala.concurrent.duration.Duration

@RunWith(classOf[JUnitRunner])
class AuthenticationSpec extends Specification {

  val request = FakeRequest(POST, "/api/authentication")

  abstract class WithUsers(app: Application = FakeApplication()) extends WithApplication(app) {

    override def around[T](t: => T)(implicit evidence$2: AsResult[T]): Result = super.around {

      try {
        DB localTx { implicit sess =>
          sql"""delete from users""".update().apply()
        }

        val userCreation = FakeRequest(POST, "/api/users")
        val payload = Json.obj(
          "username" -> "username",
          "email" -> "test@example.com",
          "password" -> "password12"
        )
        Await.result(route(userCreation.withJsonBody(payload)).get, Duration.Inf)

        t

      }finally {
        DB localTx { implicit sess =>
          sql"""delete from users""".update().apply()
        }
      }
    }
  }

  "認証API" should {

    "正しい認証情報でログインが成功すると201が返されアクセストークンが取得できる" in new WithUsers {

      val payload = Json.obj(
        "username" -> "username",
        "password" -> "password12"
      )

      val resultFuture = route(request.withJsonBody(payload)).get
      status(resultFuture) must equalTo(CREATED)
      (contentAsJson(resultFuture) \ "token").as[String] must not empty
    }

    "誤った認証情報でログインが失敗すると401が返される" in new WithUsers {

      val payload = Json.obj(
        "username" -> "username",
        "password" -> "invalidpassword"
      )

      val resultFuture = route(request.withJsonBody(payload)).get
      status(resultFuture) must equalTo(UNAUTHORIZED)
    }
  }
}
