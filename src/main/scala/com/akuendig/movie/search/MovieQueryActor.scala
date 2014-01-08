package com.akuendig.movie.search

import akka.actor.{ActorLogging, Actor}
import akka.pattern.pipe
import com.akuendig.movie.domain.PagedReleases


object MovieQueryActor {

  sealed trait MovieQueryMessages

  final case class QuerySceneReleases(year: Int, month: Int, page: Int) extends MovieQueryMessages

  final case class QuerySceneReleasesResponse(query: QuerySceneReleases,
                                              result: Option[PagedReleases]) extends MovieQueryMessages

  final case class BrowseSceneReleases(page: Int, category: SceneCategory) extends MovieQueryMessages

  final case class BrowseSceneReleasesResponse(query: BrowseSceneReleases,
                                               result: Option[PagedReleases]) extends MovieQueryMessages

}


class MovieQueryActor(xrelQueryService: XrelQueryService) extends Actor with ActorLogging {

  import MovieQueryActor._

  private implicit val _ = context.system.dispatcher

  def receive = {
    case query@QuerySceneReleases(year, month, page) =>
      val fetch = xrelQueryService.fetchSceneRelease(page, year, month)

      fetch.map {
        releases =>
          val genericReleases = PagedReleases(
            releases.pagination.currentPage,
            releases.pagination.totalPages,
            releases.pagination.perPage,
            releases.list.map(_.toRelease).to[Set]
          )

          QuerySceneReleasesResponse(query, Some(genericReleases))
      }.recover {
        case t: Throwable =>
          log.error(t, "Querying SceneReleases for query {} failed.", query)
          QuerySceneReleasesResponse(query, None)
      }.pipeTo(sender)
    case query@BrowseSceneReleases(page, category)   =>
      val fetch = xrelQueryService.browsSceneRelease(page, category)

      fetch.map {
        releases =>
          val genericReleases = PagedReleases(
            releases.pagination.currentPage,
            releases.pagination.totalPages,
            releases.pagination.perPage,
            releases.list.map(_.toRelease).to[Set]
          )

          BrowseSceneReleasesResponse(query, Some(genericReleases))
      }.recover {
        case t: Throwable =>
          log.error(t, "Browsing SceneReleases for query {} failed.", query)
          BrowseSceneReleasesResponse(query, None)
      }.pipeTo(sender)
  }
}
