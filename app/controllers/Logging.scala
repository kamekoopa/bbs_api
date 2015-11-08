package controllers

import play.api.Logger
import play.api.mvc.{Request, Result}

trait Logging { self =>

  val logger = Logger(this.getClass)

  def withRequestLogging[C, A](req: Request[C])(f: Request[C] => Result): Result = {

    val start = System.currentTimeMillis()
    val result = f(req)
    val time = System.currentTimeMillis() - start

    logger.info(s"requested at ${req.uri} - status: ${result.header.status} $time(ms)")
    logger.debug(s"request param - query: ${req.queryString} body: ${req.body.toString}")

    result
  }
}
