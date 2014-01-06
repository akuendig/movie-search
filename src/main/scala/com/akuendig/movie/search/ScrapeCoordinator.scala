package com.akuendig.movie.search

import akka.actor.{ActorLogging, Actor, ActorRef}
import com.akuendig.movie.core.StorageConfigExtension
import com.akuendig.movie.storage.ReadModel


case class ScrapingState(year: Int, month: Int, page: Int, totalPages: Int)

object ScrapeCoordinator {

  sealed trait MovieDirectoryMessage

  case object MovieDirectoryPing extends MovieDirectoryMessage

  case object MovieDirectorySnapshot extends MovieDirectoryMessage

}

class ScrapeCoordinator(queryRef: ActorRef, readModel: ActorRef) extends Actor with ActorLogging {

  import ScrapeCoordinator._
  import ReadModel._
  import MovieQueryActor._

  private var year       = 0
  private var month      = 0
  private var page       = 0
  private var totalPages = -1

  private var waitingForResponse = false

  private val storageConfig = StorageConfigExtension(context.system)

  override def preStart() {
    val state = storageConfig.scene

    year = state.year
    month = state.month
    page = state.page
    totalPages = state.totalPages
  }

  override def receive: Receive = {
    case MovieDirectoryPing if !waitingForResponse =>
      val query =
        if (totalPages >= 0 && page > totalPages) {
          if (month == 11) {
            QuerySceneReleases(year = year + 1, month = 1, page = 1)
          } else {
            QuerySceneReleases(year = year, month = month + 1, page = 1)
          }
        } else {
          QuerySceneReleases(year = year, month = month, page = page + 1)
        }

      waitingForResponse = true

      queryRef ! query
    case QuerySceneReleasesResponse(q@QuerySceneReleases(yr, mt, pg), None) =>
      waitingForResponse = false
    case QuerySceneReleasesResponse(q@QuerySceneReleases(yr, mt, pg), Some(paged)) =>
      waitingForResponse = false

      // When events are replayed then adjust the current year, month and page counters
      val correct =
        (pg == page + 1) ||
          (pg == 1 && mt == month + 1) ||
          (pg == 1 && mt == 1 && yr == year + 1)

      if (!correct) log.warning("Page not processed in sequence {}", q)

      year = yr
      month = mt
      page = pg

      totalPages = paged.totalPages

      // Update the directory
      readModel ! StoreReleases(paged.releases)
    case MovieDirectorySnapshot =>
      storageConfig.snapshotScene(ScrapingState(year, month, page, totalPages))
    case any =>
      log.warning("Unmatched message {}", any)
  }
}
