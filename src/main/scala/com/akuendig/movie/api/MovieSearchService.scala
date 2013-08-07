package com.akuendig.movie.api

import akka.actor.ActorRef
import scala.concurrent.ExecutionContext
import spray.routing.Directives
import org.json4s.DefaultJsonFormats


class MovieSearchService(directory: ActorRef)(implicit executionContext: ExecutionContext)
  extends Directives with DefaultJsonFormats {

  val route =
    path("api") {
      path("search") {
        get {
          complete("{}")
        }
      }
    }

}
