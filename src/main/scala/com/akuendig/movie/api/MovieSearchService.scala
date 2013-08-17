package com.akuendig.movie.api

import scala.concurrent.ExecutionContext
import spray.routing.Directives
import org.json4s.{NoTypeHints, Formats, DefaultJsonFormats}
import org.json4s.jackson.Serialization
import spray.httpx.Json4sJacksonSupport
import com.akuendig.movie.domain.MovieDirectoryService


class MovieSearchService(service: MovieDirectoryService)(implicit executionContext: ExecutionContext)
  extends Directives with Json4sJacksonSupport with DefaultJsonFormats {

  implicit def json4sJacksonFormats: Formats = Serialization.formats(NoTypeHints)

  val route =
    pathPrefix("api") {
      path("movies") {
        get {
          parameters('skip.as[Int] ? 0, 'take.as[Int] ? 10) {
            (skip, take) =>
              complete(service.getMovies.values.drop(skip).take(take))
          }
        }
      }
    }
}
