package com.akuendig.movie.search

import akka.actor.{ActorLogging, Actor, ActorRef}
import akka.pattern.ask
import akka.util.Timeout
import com.akuendig.movie.core.StorageConfigExtension
import com.akuendig.movie.storage.ReadModel.StoreReleases
import scala.concurrent.duration._


case class ScrapingState(year: Int, month: Int, page: Int, totalPages: Int)

object ScrapeCoordinator {

  sealed trait MovieDirectoryMessage

  case object MovieDirectoryPing extends MovieDirectoryMessage

  case object MovieDirectorySnapshot extends MovieDirectoryMessage

}

class ScrapeCoordinator(queryRef: ActorRef, readModel: ActorRef) extends Actor with ActorLogging {

  import ScrapeCoordinator._
  import MovieQueryActor._

  private var pageCount  = 0
  private var totalPages = -1

  private var waitingForResponse = false

  private val storageConfig = StorageConfigExtension(context.system)

  override def preStart() {
    log.info("Starting up")

    val (pc, tp) = storageConfig.movies
    pageCount = pc
    totalPages = tp

    log.info("Continuing from pageCount: {} totalPages: {}",
      pageCount, totalPages
    )
  }

  // We start downloading from the back and `page` is actually our
  // count of downloaded pages.
  def inversePage: Int =
    Math.max(1, totalPages - pageCount)

  override def receive: Receive = {
    case MovieDirectoryPing if !waitingForResponse =>
      val query =
        if (totalPages < 0)
          BrowseSceneReleases(0, SceneCategoryMovies)
        else
          BrowseSceneReleases(inversePage, SceneCategoryMovies)

      waitingForResponse = true

      queryRef ! query
    case QuerySceneReleasesResponse(_, None)
         | BrowseSceneReleasesResponse(_, None) =>
      waitingForResponse = false
    case BrowseSceneReleasesResponse(query, Some(paged)) =>
      waitingForResponse = false

      // Only increment our page counter if we are certain, that we
      // did not miss a page. For example:
      // current(page = 12, totalPages = 100) => browse(88)
      // response(page = 88, totalPages = 102) => we missed 90 and 89
      //  because we have two new pages. Simplest solution is to just
      //  requery page 102 - 12 == 90.
      if (paged.totalPages == totalPages) {
        pageCount = paged.page + 1
      } else {
        log.warning(s"The was a new page created since the last query. Total pages {} => {}", totalPages,
          paged.totalPages)
      }

      totalPages = paged.totalPages

      // We store the releases anyway.
      implicit val _timeout = Timeout(5.seconds)
      readModel ? StoreReleases(paged.releases)
    case MovieDirectorySnapshot =>
      storageConfig.snapshotMovies(pageCount, totalPages)
    case any =>
      log.warning("Unmatched message {}", any)
  }
}
