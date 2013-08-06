package com.akuendig.movie.search

import org.specs2.mutable.Specification
import scala.concurrent.Future
import spray.http.{StatusCodes, HttpRequest, HttpResponse}


class QueryServiceSpec extends Specification {
  "QueryService" should {
    "query correct address" in {
      var request: HttpRequest = null

      val component = new XrelQueryComponentImpl {
        implicit val executionContext = scala.concurrent.ExecutionContext.Implicits.global

        val sendReceive: HttpRequest => Future[HttpResponse] = req => {
          request = req
          Future(HttpResponse(status = StatusCodes.OK))
        }
      }

      component.xrelQueryService.fetchSceneRelease(1, 2013, 12)
      request.uri.toString.mustEqual("http://api.xrel.to/api/release/browse_category.json?page=1&per_page=100&archive=2013-12").orThrow

      component.xrelQueryService.fetchSceneRelease(1, 203, 1)
      request.uri.toString.mustEqual("http://api.xrel.to/api/release/browse_category.json?page=1&per_page=100&archive=0203-01").orThrow
    }
  }
}
