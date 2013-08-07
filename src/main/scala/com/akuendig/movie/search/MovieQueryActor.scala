package com.akuendig.movie.search

import akka.actor.{ActorLogging, Actor}
import org.eligosource.eventsourced.core.Receiver
import spray.http.{HttpResponse, HttpRequest}
import scala.concurrent.Future
import com.akuendig.movie.search.xrel.PagedSceneReleases
import com.akuendig.movie.search.domain.PagedReleases


object MovieQueryActor {

  sealed trait MovieQueryMessage

  case class QuerySceneMovies(year: Int, month: Int, page: Int) extends MovieQueryMessage

  case class QuerySceneMoviesResponse(query: QuerySceneMovies, movies: PagedReleases) extends MovieQueryMessage

}


class MovieQueryActor(xrelQueryService: XrelQueryService) extends Actor with ActorLogging {
  this: Receiver  =>

  import MovieQueryActor._

  private implicit val _ = context.system.dispatcher

  val sendReceive: HttpRequest => Future[HttpResponse] = spray.client.pipelining.sendReceive

  def receive = {
    case query@QuerySceneMovies(year, month, page) =>
      val msg = message
      val sndr = sender

      val fetch = xrelQueryService.fetchSceneRelease(page, year, month)

      fetch.onSuccess {
        case releases: PagedSceneReleases =>
          val genericReleases = PagedReleases(
            releases.pagination.currentPage,
            releases.pagination.totalPages,
            releases.pagination.perPage,
            releases.list.map(_.toRelease)
          )
          val response = msg.copy(event = QuerySceneMoviesResponse(query, genericReleases))
          sndr ! response
          msg.confirm(true)
      }

      fetch.onFailure {
        case t: Throwable =>
          log.error(t, s"Failure to fetch latest scene releases with query $query")
          msg.confirm(false)
      }
  }
}
