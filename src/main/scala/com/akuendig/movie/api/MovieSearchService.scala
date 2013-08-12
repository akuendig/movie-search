package com.akuendig.movie.api

import scala.concurrent.ExecutionContext
import spray.routing.Directives
import com.akuendig.movie.search.MovieDirectoryService
import org.json4s.{NoTypeHints, Formats, DefaultJsonFormats}
import spray.httpx.Json4sJacksonSupport
import org.json4s.jackson.Serialization


class MovieSearchService(service: MovieDirectoryService)(implicit executionContext: ExecutionContext)
  extends Directives with Json4sJacksonSupport with DefaultJsonFormats {

  implicit def json4sJacksonFormats: Formats = Serialization.formats(NoTypeHints)

  val route =
    path("api/search") {
          get {
          parameters('skip.as[Int] ? 0, 'take.as[Int] ? 10) {
            (skip, take) =>
              complete(service.getMovies.values.drop(skip).take(take))
          }
        }

    }
}
