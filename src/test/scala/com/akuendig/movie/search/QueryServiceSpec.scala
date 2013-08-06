package com.akuendig.movie.search

import org.specs2.mutable.Specification
import scala.concurrent.Future
import spray.http.{StatusCodes, HttpRequest, HttpResponse}


class QueryServiceSpec extends Specification {
  "QueryService" should {
    "query correct address" in {
      var request: HttpRequest = null

      val mockedService = new QueryService() {
        implicit val executionContext = scala.concurrent.ExecutionContext.Implicits.global

        val sendReceive: HttpRequest => Future[HttpResponse] = req => {
          request = req
          Future(HttpResponse(status = StatusCodes.OK))
        }
      }

      mockedService.fetchSceneRelease("testCategory", "testType", 1)
      request.uri.toString.mustEqual("http://api.xrel.to/api/release/browse_category.json?page=1&per_page=100&ext_info_type=testType&category_name=testCategory")
    }
  }
}
