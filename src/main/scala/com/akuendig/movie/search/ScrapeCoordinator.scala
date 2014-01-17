package com.akuendig.movie.search

import akka.actor.{ActorLogging, Actor, ActorRef}
import akka.pattern.ask
import akka.util.Timeout
import com.akuendig.movie.core.StorageConfigExtension
import com.akuendig.movie.storage.ReadModel
import scala.concurrent.duration._


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

  import org.json4s._
  import org.json4s.jackson.JsonMethods._

  implicit val _formats = DefaultFormats

  private var year       = 0
  private var month      = 0
  private var page       = 0
  private var totalPages = -1

  def currentScrapingState = ScrapingState(year, month, page, totalPages)

  private var unfinished = Set.empty[ScrapingState]

  private var waitingForResponse = false

  private val storageConfig = StorageConfigExtension(context.system)

  override def preStart() {
    log.info("Starting up")

    val (state, extra: String) = storageConfig.scene

    year = state.year
    month = state.month
    page = state.page
    totalPages = state.totalPages

    unfinished = parse(StringInput(extra)).extract[Set[ScrapingState]]

    log.info("Continuing from year: {} month: {} page: {} totalPages: {}",
      year, month, page, totalPages
    )
  }

  override def receive: Receive = {
    case MovieDirectoryPing if !waitingForResponse =>
      def nextPage = QuerySceneReleases(year = year, month = month, page = page + 1)
      def nextMonth = {
        val m = month + 1
        QuerySceneReleases(year = year + m / 12, month = m % 12, page = 1)
      }

      val query =
        if (totalPages >= 0) {
          if (page >= 50 && totalPages > 50) {
            unfinished += currentScrapingState
            nextMonth
          } else if (page > totalPages) {
            nextMonth
          } else {
            nextPage
          }
        } else {
          nextPage
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

      // Update the directory, ignore the response but silent logger warning
      implicit val _timeout = Timeout(5.seconds)
      readModel ? StoreReleases(paged.releases)
    case MovieDirectorySnapshot =>
      println(currentScrapingState)
      val json = compact(render(unfinished.asJValue))

      storageConfig.snapshotScene(currentScrapingState, json)
    case any =>
      log.warning("Unmatched message {}", any)
  }
}
