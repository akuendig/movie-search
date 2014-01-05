package com.akuendig.movie.api

import spray.routing.Directives
import spray.http.HttpMethods._
import akka.actor.ActorSystem
import akka.event.Logging
import com.akuendig.movie.storage.MovieStorage
import com.akuendig.movie.domain.Release
import com.akuendig.movie.domain.SerializationFormats


class MovieSearchService(implicit val system: ActorSystem)
  extends Directives with CORSDirectives with SerializationFormats.JsonFormats {

  self: MovieStorage =>

  import scala.pickling._
  import scala.pickling.json._

  private val log = Logging(system, getClass)
  private val origin = "http://localhost:9001"

  private implicit val _ec = system.dispatcher

  val route =
    (pathPrefix("api") & path("movies") & cqrsAllow(origin)(GET)) {
      get {
        parameters('skip.as[Int] ? 0, 'take.as[Int] ? 10) {
          (skip, take) =>
            val result = getMovies(skip, take)

            complete(result.map {
              releases: Seq[Release] =>
                val pickled = releases.pickle
                pickled.value
            })
        }
      }
    }
}
