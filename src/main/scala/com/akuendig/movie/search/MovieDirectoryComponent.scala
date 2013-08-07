package com.akuendig.movie.search

import akka.actor.{ActorLogging, ActorRef, Actor}
import org.eligosource.eventsourced.core.{Message, Receiver, Confirm, Eventsourced}
import scala.concurrent.duration.{Duration, FiniteDuration}
import spray.http.DateTime

object MovieDirectoryActor {

  sealed trait MovieDirectoryMessage

  case object MovieDirectoryPing

}

trait MovieDirectoryComponent {
  this: MovieQueryComponent with XrelQueryComponent =>

  import XrelQueryModels._

  class ReleaseDirectory{
    private var data = Map.empty[String, Release]

    def put(release: Release) {
      data += release.id -> release
    }

    def putAll(releases: Traversable[Release]) {
      releases.foreach(put)
    }

    def all = data
  }

  class MovieDirectoryActor(query: ActorRef) extends Actor with ActorLogging {
    this: Receiver with Confirm with Eventsourced =>

    import MovieQueryActor._
    import MovieDirectoryActor._

    val startedAt = DateTime.now

    val directory = new ReleaseDirectory()

    var year = startedAt.year
    var month = startedAt.month
    var page = 0
    var totalPages = 0

    def receive = {
      case MovieDirectoryPing =>
        page += 1

        if (totalPages > 0 && page > totalPages) {
          page = 1
          month -= 1

          if (month < 1) {
            month = 12
            year -= 1
          }
        }

        query ! Message(QuerySceneMovies(year = year, month = month, page = page))
      case QuerySceneMoviesResponse(QuerySceneMovies(yr, mt, pg), paged) =>
        // When events are replayed then adjust the current year, month and page counters
        if (yr < year) year = yr
        if (yr == year && mt < month) month = mt
        if (yr == year && mt == month && pg > page) page = pg

        if (yr == year && mt == month) totalPages = paged.totalPages

        directory.putAll(paged.releases)

        println(s"Updated directory with ${paged.releases.size} entries, directory now contains ${directory.all.size} entries")
    }
  }

}
