package com.akuendig.movie.storage

import akka.actor.{ActorLogging, Actor, ActorRef}
import spray.http.DateTime
import com.akuendig.movie.domain.{QuerySceneReleasesResponse, QuerySceneReleases}


object MovieDirectoryActor {

  sealed trait MovieDirectoryMessage
  case object MovieDirectoryPing
}

class MovieDirectoryActor(queryRef: ActorRef, readModel: ActorRef) extends Actor with ActorLogging {

  import MovieDirectoryActor._
  import MongoDbReadModel._

  val startedAt = DateTime.now

  var receivedPages = Set.empty[(Int, Int, Int)]

  val now = DateTime.now

  var year = now.year
  var month = now.month
  var page = 0
  var totalPages = -1

  var waitingForResponse = false

  //  val db = JdbcBackend.Database.forURL("jdbc:h2:mem:test1", driver = "org.h2.Driver")
  //  val backend = new SlickBackend(scala.slick.driver.H2Driver, AnnotationMapper)

  override def receive: Receive = {
    case MovieDirectoryPing if !waitingForResponse =>
      val query =
        if (totalPages >= 0 && page > totalPages) {
          if (month == 1) {
            QuerySceneReleases(year = year - 1, month = 12, page = 1)
          } else {
            QuerySceneReleases(year = year, month = month - 1, page = 1)
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
          (pg == 1 && mt == month - 1) ||
          (pg == 1 && mt == 12 && yr == year - 1)

      if (!correct) log.warning("Page not processed in sequence {}", q)

      year = yr
      month = mt
      page = pg

      totalPages = paged.totalPages

      // Update the directory
      readModel ! StoreReleases(paged.releases)
    case any =>
      log.warning("Unmatched message {}", any)
  }
}
