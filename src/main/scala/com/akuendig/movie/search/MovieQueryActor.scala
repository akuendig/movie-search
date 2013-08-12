package com.akuendig.movie.search

import akka.actor.{ActorLogging, Actor}
import com.akuendig.movie.search.domain.PagedReleases
import scala.util.{Failure, Success}
import org.eligosource.eventsourced.core.Message


object MovieQueryActor {
  type QuerySceneReleases = domain.QuerySceneReleases
  val QuerySceneReleases = domain.QuerySceneReleases
  type QuerySceneReleasesResponse = domain.QuerySceneReleasesResponse
  val QuerySceneReleasesResponse = domain.QuerySceneReleasesResponse
}


class MovieQueryActor(xrelQueryService: XrelQueryService) extends Actor with ActorLogging {

  import MovieQueryActor._
  private implicit val _ = context.system.dispatcher

  def receive = {
    case query@QuerySceneReleases(year, month, page) =>
      val sndr = sender

      val fetch = xrelQueryService.fetchSceneRelease(page, year, month)

      fetch.onComplete {
        case Success(releases) =>
          val genericReleases = PagedReleases(
            releases.pagination.currentPage,
            releases.pagination.totalPages,
            releases.pagination.perPage,
            releases.list.map(_.toRelease).to[Vector]
          )

          sndr ! Message(QuerySceneReleasesResponse(query, Some(genericReleases)))
        case Failure(t) =>
          sndr ! QuerySceneReleasesResponse(query)

          log.error(t, "Querying SceneReleases for query {} failed.", query)
      }
  }
}
