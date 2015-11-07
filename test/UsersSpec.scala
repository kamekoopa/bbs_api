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

@RunWith(classOf[JUnitRunner])
class UsersSpec extends Specification {

  val createRequest = FakeRequest(POST, "/api/users")
  val searchRequest = (id: Int) => FakeRequest(GET, s"/api/users/$id")

  abstract class WithoutUsers(app: Application = FakeApplication()) extends WithApplication(app) {

    override def around[T](t: => T)(implicit evidence$2: AsResult[T]): Result = super.around {

      try {
        DB localTx { implicit sess =>
          sql"""delete from users""".update().apply()
        }

        t

      }finally {
        DB localTx { implicit sess =>
          sql"""delete from users""".update().apply()
        }
      }
    }
  }

  abstract class WithUsers(app: Application = FakeApplication()) extends WithApplication(app) {

    override def around[T](t: => T)(implicit evidence$2: AsResult[T]): Result = super.around {

      try {
        DB localTx { implicit sess =>
          sql"""delete from users""".update().apply()
          sql"""insert into users(id, username, password, email, salt) values (1, 'username', 'password12','test@example.com', 'salt')"""
            .update().apply()
        }

        t

      }finally {
        DB localTx { implicit sess =>
          sql"""delete from users""".update().apply()
        }
      }
    }
  }


  "ユーザが存在しない時" should {

    "ユーザ作成APIはユーザ名が指定されていなければ400エラーを返す" in new WithApplication {

      val payload = Json.obj(
        "email" -> "test@example.com",
        "password" -> "password12"
      )

      val resultFuture = route(createRequest.withJsonBody(payload)).get
      status(resultFuture) must equalTo(BAD_REQUEST)
    }

    "ユーザ作成APIはユーザ名が11文字以上なら400エラーを返す" in new WithApplication {

      val payload = Json.obj(
        "username" -> "12345678901",
        "email" -> "test@example.com",
        "password" -> "password12"
      )

      val resultFuture = route(createRequest.withJsonBody(payload)).get
      status(resultFuture) must equalTo(BAD_REQUEST)
    }

    "ユーザ作成APIはemailが指定されていなければ400エラーを返す" in new WithApplication {

      val payload = Json.obj(
        "username" -> "username",
        "password" -> "password12"
      )

      val resultFuture = route(createRequest.withJsonBody(payload)).get
      status(resultFuture) must equalTo(BAD_REQUEST)
    }

    "ユーザ作成APIはパスワードが指定されていなければ400エラーを返す" in new WithApplication {

      val payload = Json.obj(
        "username" -> "username",
        "email" -> "test@example.com"
      )

      val resultFuture = route(createRequest.withJsonBody(payload)).get
      status(resultFuture) must equalTo(BAD_REQUEST)
    }

    "ユーザ作成APIはパスワードが9文字以下なら400エラーを返す" in new WithApplication {

      val payload = Json.obj(
        "username" -> "username",
        "email" -> "test@example.com",
        "password" -> "password1"
      )

      val resultFuture = route(createRequest.withJsonBody(payload)).get
      status(resultFuture) must equalTo(BAD_REQUEST)
    }

    "ユーザ作成APIはユーザが作成されると202で作成されたユーザの情報を返す" in new WithoutUsers {

      val payload = Json.obj(
        "username" -> "username",
        "email" -> "test@example.com",
        "password" -> "password12"
      )

      val resultFuture = route(createRequest.withJsonBody(payload)).get

      status(resultFuture) must equalTo(ACCEPTED)

      val created = contentAsJson(resultFuture)
      (created \ "id").asOpt[Int] must beSome
      (created \ "username").as[String] must equalTo("username")
      (created \ "email").as[String] must equalTo("test@example.com")
    }

    "ユーザ取得APIは404を返す" in new WithoutUsers {

      val resultFuture = route(searchRequest(1)).get

      status(resultFuture) must equalTo(NOT_FOUND)
    }
  }

  "ユーザが存在している時" should {

    "ユーザ作成APIは同一ユーザIDを指定されると409を返す" in new WithUsers {

      val payload = Json.obj(
        "username" -> "username",
        "email" -> "test@example.com",
        "password" -> "password12"
      )

      val resultFuture = route(createRequest.withJsonBody(payload)).get

      status(resultFuture) must equalTo(CONFLICT)
    }

    "ユーザ取得APIは指定IDのユーザ情報を返す" in new WithUsers {

      val resultFuture = route(searchRequest(1)).get

      status(resultFuture) must equalTo(OK)

      val result = contentAsJson(resultFuture)
      (result \ "id").as[Int] must equalTo(1)
      (result \ "username").as[String] must equalTo("username")
      (result \ "email").as[String] must equalTo("test@example.com")
    }
  }
}
