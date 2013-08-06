package com.akuendig.movie.search

import scala.concurrent.{ExecutionContext, Future}
import spray.client.pipelining._
import spray.http.{StringRendering, Uri, HttpResponse, HttpRequest}
import spray.http.Uri.Query


trait XrelQueryComponentImpl extends XrelQueryComponent {

  implicit val executionContext: ExecutionContext

  val sendReceive: HttpRequest => Future[HttpResponse]

  val xrelQueryService = new QueryService()

  class QueryService extends XrelQueryService {

    def uriFromAddressAndParams(address: String, params: Map[String, String]) =
      Uri(address).copy(query = Query(params)).render(new StringRendering).get

    def fetchSceneRelease(page: Int, year: Int, month: Int): Future[Seq[SceneRelease]] = {
      val address = "http://api.xrel.to/api/release/browse_category.json"
      val params = Map[String, String](
        "page" -> page.toString,
        "per_page" -> "100",
        "archive" -> f"$year%04d-$month%02d"
      )

      val req = Get(uriFromAddressAndParams(address, params))
      println(req)
      val response = sendReceive(req)

      response.map {
        data =>
          println(data)
          Seq()
      }

      //    var data struct {
      //      Payload struct {
      //        TotalCount int32
      //          Pagination struct {
      //          CurrentPage int32
      //            PerPage     int32
      //            TotalPages  int32
      //        }
      //        List []SceneRelease
      //      }
      //    }
    }

    def fetchDetailedSceneRelease(id: String): Future[DetailedSceneRelease] = {
      val address = "http://api.xrel.to/api/release/info.json"
      var params = Map[String, String](
        "id" -> id
      )

      // var data struct {
      //  Payload DetailedSceneRelease
      // }
      ???
    }

    def fetchP2PCategories(): Future[Seq[Category]] = {
      val address = "http://api.xrel.to/api/p2p/categories.json"

      // var data struct {
      //  Payload Seq[Category]
      // }
      ???
    }

    def fetchP2PRelease(page: Int, catId: String): Future[Seq[P2PRelease]] = {
      val address = "http://api.xrel.to/api/p2p/releases.json"
      val params = Map[String, String](
        "page" -> page.toString,
        "per_page" -> "100",
        "category_id" -> catId
      )

      val pipeline: HttpRequest => Future[HttpResponse] = sendReceive
      val req = Get(uriFromAddressAndParams(address, params))
      println(req)
      val response = pipeline(req)

      response.map {
        data =>
          println(data)
          Seq()
      }

      //    var data struct {
      //      Payload struct {
      //        TotalCount int32
      //          Pagination struct {
      //          CurrentPage int32
      //            PerPage     int32
      //            TotalPages  int32
      //        }
      //        List []P2PRelease
      //      }
      //    }
    }

    def fetchDetailedP2PRelease(id: String): Future[DetailedP2PRelease] = {
      val address = "http://api.xrel.to/api/p2p/rls_info.json"
      var params = Map[String, String](
        "id" -> id
      )

      // var data struct {
      //  Payload DetailedSceneRelease
      // }
      ???
    }

    def fetchRateLimit(): Future[RateLimit] = {
      val address = "http://api.xrel.to/api/user/rate_limit_status.json"

      // var data struct {
      //  Payload RateLimit
      // }
      ???
    }
  }

}
