package com.akuendig.movie.search

import akka.actor.{Actor, ActorRef}
import org.eligosource.eventsourced.core.{SnapshotOffer, SnapshotRequest, Eventsourced, Receiver}
import spray.http.DateTime
import scala.concurrent.stm.Ref
import com.akuendig.movie.domain.{QuerySceneReleasesResponse, QuerySceneReleases, Release}
import spray.util.SprayActorLogging
import com.akuendig.movie.core.IterableBackedSeq


object MovieDirectoryActor {

  sealed trait MovieDirectoryMessage

  case object MovieDirectoryPing

  val MovieDirectorySnapshot = com.akuendig.movie.domain.MovieDirectorySnapshot
  type MovieDirectorySnapshot = com.akuendig.movie.domain.MovieDirectorySnapshot

}

class MovieDirectoryActor(queryRef: ActorRef, movieDirectory: Ref[Map[String, Release]]) extends Actor with SprayActorLogging {
  this: Receiver with Eventsourced =>

  //  import MovieQueryActor._

  import MovieDirectoryActor._

  val startedAt = DateTime.now

  var receivedPages = Set.empty[(Int, Int, Int)]

  val now = DateTime.now

  var year = now.year
  var month = now.month
  var page = 0
  var totalPages = -1

  var waitingForResponse = false

  def receive: Receive = {
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
      val releases = paged.releases
      val directory = movieDirectory.single.transformAndGet(_ ++ (releases.map(_.id), releases).zipped)

      // Remember which pages are fetched
      receivedPages += ((yr, month, page))

      log.info(
        "Updated directory with {} entries from query {}, directory now contains {} entries.",
        releases.size, q, directory.size
      )
    case sr: SnapshotRequest =>
      sr.process(MovieDirectorySnapshot(
        year = year,
        month = month,
        page = page,
        totalPages = totalPages,
        releases = movieDirectory.single.get.values.to[Set]
      ))
    case so: SnapshotOffer =>
      so.snapshot.state match {
        case MovieDirectorySnapshot(yr, mt, pg, tp, ms) =>
          year = yr
          month = mt
          page = pg
          totalPages = tp
          movieDirectory.single.set((ms.map(_.id), ms).zipped.toMap)

          log.info("Successfully recovered from {}", SnapshotOffer(so.snapshot.copy(state = "State removed for logging")))
        case _ =>
          log.error("Snapshot offer can not be processed: {}", SnapshotOffer(so.snapshot.copy(state = "State removed for logging")))
      }
    case any =>
      log.warning("Unmatched message {}", any)
  }
}
