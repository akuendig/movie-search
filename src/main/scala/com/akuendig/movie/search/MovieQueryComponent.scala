package com.akuendig.movie.search

import akka.actor.Actor
import org.eligosource.eventsourced.core.Receiver
import scala.concurrent.Future
import spray.http.HttpRequest
import spray.http.HttpResponse


trait MovieQueryComponent {
  this: XrelQueryComponent =>

  object MovieQueryActor {

    sealed trait MovieQueryMessage

    case class QueryMovies(year: Int, page: Int) extends MovieQueryMessage

    case class QueryMoviesResponse(query: QueryMovies, movies: Seq[Movie])

  }

  class MovieQueryActor extends Actor {
    this: Receiver =>

    import MovieQueryActor._

    implicit val executionContext = context.system.dispatcher

    val sendReceive: HttpRequest => Future[HttpResponse] = spray.client.pipelining.sendReceive

    def receive = {
      case query@QueryMovies(year, page) =>
        val msg = message
        val sndr = sender



        val movies = Seq(Movie(id = "0", title = "Dummy 0"), Movie(id = "1", title = "Dummy 1"))
        val response = message.copy(event = QueryMoviesResponse(query, movies))

        sender ! response

        message.confirm(true)
    }
  }

}




