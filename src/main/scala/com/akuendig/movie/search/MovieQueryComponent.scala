package com.akuendig.movie.search

import akka.actor.{ActorLogging, Actor}
import org.eligosource.eventsourced.core.Receiver
import scala.concurrent.Future
import spray.http.HttpRequest
import spray.http.HttpResponse

object MovieQueryActor {
  import XrelQueryModels._

  sealed trait MovieQueryMessage

  case class QuerySceneMovies(year: Int, month: Int, page: Int) extends MovieQueryMessage

  case class QuerySceneMoviesResponse(query: QuerySceneMovies, movies: PagedReleases) extends MovieQueryMessage

}

trait MovieQueryComponent {
  this: XrelQueryComponent =>

  import XrelQueryModels._

  class MovieQueryActor extends Actor with ActorLogging {
    this: Receiver =>

    import MovieQueryActor._

    implicit val executionContext = context.system.dispatcher

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

}




