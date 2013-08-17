package com.akuendig.movie.search

import akka.actor.ActorSystem
import akka.event.Logging
import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}
import spray.http.{HttpResponse, StringRendering, Uri}
import spray.http.Uri.Query
import spray.httpx.RequestBuilding._
import com.akuendig.movie.search.xrel._


abstract class XrelQueryServiceImpl(implicit val system: ActorSystem) extends XrelQueryService with SendReceive {
  val log = Logging(system, getClass)

  import org.json4s._
  import org.json4s.jackson.JsonMethods._

  private implicit val _fmts: Formats = DefaultFormats
  private implicit val _ec: ExecutionContext = system.dispatcher

  def uriFromAddressAndParams(address: String, params: Map[String, String]) =
    Uri(address).copy(query = Query(params)).render(new StringRendering).get

  def fixFields(json: JValue): JValue =
    json.transformField {
      case ("link_href", x) => ("linkHref", x)
      case ("audio_type", x) => ("audioType", x)
      case ("video_type", x) => ("videoType", x)
      case ("group_name", x) => ("groupName", x)
      case ("ext_info", x) => ("extInfo", x)
      case ("type", x) => ("tpe", x)
      case ("total_count", x) => ("totalCount", x)
      case ("current_page", x) => ("currentPage", x)
      case ("per_page", x) => ("perPage", x)
      case ("total_pages", x) => ("totalPages", x)
      case ("tv_season", x) => ("tvSeason", x)
      case ("tv_episode", x) => ("tvEpisode", x)
    }

  def fetchSceneRelease(page: Int, year: Int, month: Int): Future[PagedSceneReleases] = {
    val address = "http://api.xrel.to/api/release/latest.json"
    val params = Map[String, String](
      "page" -> page.toString,
      "per_page" -> "100",
      "archive" -> f"$year%04d-$month%02d"
    )

    val req = Get(uriFromAddressAndParams(address, params))
    val response: Future[HttpResponse] = sendReceive(req)(5.seconds)

    response.map {
      data =>
        val jsonData = fixFields(parse(data.entity.asString.lines.drop(1).next)) \ "payload"
        val totalCount = (jsonData \ "totalCount").extract[Int]
        val pagination = (jsonData \ "pagination").extract[Pagination]
        val releasesJArray = (jsonData \ "list").asInstanceOf[JArray]

        val releases = releasesJArray.arr.foldLeft(Seq.newBuilder[SceneRelease]) {
          (builder, rel) =>
            try {
              val release = rel.extract[SceneRelease]
              builder += release
            } catch {
              case e: MappingException => log.error(e, s"Reading SceneRelease failed. ${compact(render(rel))}")
            }

            builder
        }.result

        log.info("{}\n{}", releases.head, (fixFields(parse(data.entity.asString.lines.drop(1).next)) \ "payload" \ "list").asInstanceOf[JArray].arr.head)

        PagedSceneReleases(totalCount, pagination, releases)
    }
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

  def fetchP2PRelease(page: Int, catId: String): Future[PagedP2PReleases] = {
    val address = "http://api.xrel.to/api/p2p/releases.json"
    val params = Map[String, String](
      "page" -> page.toString,
      "per_page" -> "100",
      "category_id" -> catId
    )

    val req = Get(uriFromAddressAndParams(address, params))
    val response = sendReceive(req)(5.seconds)

    response.map {
      data =>
        val jsonData = parse(data.entity.asString.lines.drop(1).next) \ "payload"
        val releases = fixFields(jsonData).extract[PagedP2PReleases]

        releases
    }
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
