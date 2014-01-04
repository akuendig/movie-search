package com.akuendig.movie.api

import spray.routing.Directives
import akka.actor.ActorSystem

// Trait for serving static resources
// Sends 404 for 'favicon.icon' requests and serves static resources in 'bootstrap' folder.
class StaticResources(implicit system: ActorSystem)
  extends Directives {

  val route =
    get {
      pathPrefix("") {
        getFromDirectory("/Users/adrian/Documents/work/movie-search/src/main/webapp/dist")
//        getFromResourceDirectory("webapp/")
      } ~ path("") {
        getFromFile("/Users/adrian/Documents/work/movie-search/src/main/webapp/dist/index.html")
//        getFromResource("webapp/index.html")
      }
    }

}
