package com.akuendig.movie.search

import akka.actor.{ActorRef, Actor}
import org.eligosource.eventsourced.core.{Message, Receiver, Confirm, Eventsourced}

trait MovieDirectoryComponent {
  this: MovieQueryComponent =>

  object MovieDirectoryActor {

    sealed trait MovieDirectoryMessage

    case object MovieDirectoryPing

  }

  class MovieDirectoryActor(query: ActorRef) extends Actor {
    this: Receiver with Confirm with Eventsourced =>

    import MovieQueryActor._
    import MovieDirectoryActor._

    var year = 2013
    var page = 0

    def receive = {
      case ping@MovieDirectoryPing =>
        println(s"received $ping")

        page += 1
        if (page > 50) {
          page = 1
          year -= 1
        }

        query ! Message(QueryMovies(year, page))
      case resp@QueryMoviesResponse(QueryMovies(yr, pg), movies) =>
        println(s"reveiced $resp")

        // When events are replayed
        if (yr < year) year = yr
        if (pg > page) page = pg
    }
  }

}
