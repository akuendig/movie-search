package com.akuendig.movie.search

import akka.actor.Actor
import org.eligosource.eventsourced.core.Receiver
import scala.concurrent.{ExecutionContext, Future}
import spray.http.{StringRendering, Uri, HttpResponse, HttpRequest}
import spray.http.Uri.Query


sealed trait MovieQueryMessage
case class QueryMovies(year: Int, page: Int) extends MovieQueryMessage
case class QueryMoviesResponse(query: QueryMovies, movies: Seq[Movie])

class MovieQueryActor extends Actor with QueryService { this: Receiver =>
  implicit val executionContext = context.system.dispatcher

  val sendReceive: HttpRequest => Future[HttpResponse] = spray.client.pipelining.sendReceive

  def receive = {
    case query @ QueryMovies(year, page) =>
      println(s"received $query")

      val movies = Seq(Movie(id = "0", title = "Dummy 0"), Movie(id = "1", title = "Dummy 1"))
      val response = message.copy(event = QueryMoviesResponse(query, movies))

      sender ! response

      message.confirm(true)
  }
}

trait QueryService {
  import spray.client.pipelining._

  implicit val executionContext: ExecutionContext

  val sendReceive: HttpRequest => Future[HttpResponse]

  def uriFromAddressAndParams(address: String, params: Map[String, String]) =
    Uri(address).copy(query = Query(params)).render(new StringRendering).get

  def fetchSceneRelease(category: String, tpe: String, page: Int): Future[Seq[SceneRelease]] = {
    val address = "http://api.xrel.to/api/release/browse_category.json"
    var params = Map[String, String](
      "page" -> page.toString,
      "per_page" -> "100",
      "ext_info_type" -> tpe // movie|tv|game|console|xxx
    )

    if (category != "") {
      params = params.updated("category_name", category)
    }

    val pipeline: HttpRequest => Future[HttpResponse] = sendReceive
    val req = Get(uriFromAddressAndParams(address, params))
    println(req)
    val response = pipeline(req)

    response.map{ data =>
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

  def fetchP2PRelease(catId: String, page: Int): Future[Seq[P2PRelease]] = {
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

    response.map{ data =>
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
