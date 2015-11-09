package models.post

import models.auth.AccessToken

case class ThreadCreationRequest(title: String, tagNames: Seq[String], token: String) {

  val accessToken = new AccessToken(token)
}
