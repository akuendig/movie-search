package com.akuendig.movie.api

import spray.routing.Directives
import spray.http.HttpMethods._
import akka.actor.ActorSystem
import akka.event.Logging
import com.akuendig.movie.storage.MovieStorage
import spray.httpx.Json4sJacksonSupport
import org.json4s.DefaultFormats


class MovieSearchService(storage: MovieStorage)(implicit val system: ActorSystem)
  extends Directives with CORSDirectives with Json4sJacksonSupport {

  val json4sJacksonFormats = DefaultFormats

  private val log = Logging(system, getClass)
  private val origin = "http://localhost:9001"

  private implicit val _ec = system.dispatcher

  val route =
    (pathPrefix("api") & path("movies") & cqrsAllow(origin)(GET)) {
      get {
        parameters('skip.as[Int] ? 0, 'take.as[Int] ? 10) {
          (skip, take) =>
            val result = storage.get(skip, take)

            complete(result)
        }
      }
    }
}
