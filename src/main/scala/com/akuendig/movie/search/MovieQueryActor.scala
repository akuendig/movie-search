package com.akuendig.movie.search

import akka.actor.{ActorLogging, Actor}
import scala.util.{Failure, Success}
import com.akuendig.movie.domain.PagedReleases


object MovieQueryActor {

  sealed trait MovieQueryMessages

  final case class QuerySceneReleases(var year: Int = 0, var month: Int = 0,
                                      var page: Int = 0) extends MovieQueryMessages

  final case class QuerySceneReleasesResponse(var query: QuerySceneReleases = QuerySceneReleases(),
                                              var result: Option[PagedReleases] = None) extends MovieQueryMessages

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
            releases.list.map(_.toRelease).to[Set]
          )

          sndr ! QuerySceneReleasesResponse(query, Some(genericReleases))
        case Failure(t) =>
          sndr ! QuerySceneReleasesResponse(query)

          log.error(t, "Querying SceneReleases for query {} failed.", query)
      }
  }
}
