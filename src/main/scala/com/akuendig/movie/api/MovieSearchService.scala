package com.akuendig.movie.api

import spray.routing.Directives
import spray.http.HttpMethods._
import org.json4s.{NoTypeHints, Formats, DefaultJsonFormats}
import org.json4s.jackson.Serialization
import spray.httpx.Json4sJacksonSupport
import com.akuendig.movie.domain.MovieDirectoryService
import akka.actor.ActorSystem
import akka.event.Logging


class MovieSearchService(service: MovieDirectoryService)(implicit val system: ActorSystem)
  extends Directives with CORSDirectives with Json4sJacksonSupport with DefaultJsonFormats {

  private val log = Logging(system, getClass)
  private val origin = "http://localhost:9001"

  private implicit val _ec = system.dispatcher

  def json4sJacksonFormats: Formats = Serialization.formats(NoTypeHints)

  val route =
    (pathPrefix("api") & path("movies") & cqrsAllow(origin)(GET)) {
      get {
        parameters('skip.as[Int] ? 0, 'take.as[Int] ? 10) {
          (skip, take) =>
            val result = service.getMovies(skip, take)

            complete(result)
        }
      }
    }
}
