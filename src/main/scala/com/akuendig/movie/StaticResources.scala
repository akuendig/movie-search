package com.akuendig.movie

import spray.routing.Directives
import org.json4s.DefaultJsonFormats
import akka.actor.ActorSystem

// Trait for serving static resources
// Sends 404 for 'favicon.icon' requests and serves static resources in 'bootstrap' folder.
class StaticResources(implicit system: ActorSystem)
  extends Directives with DefaultJsonFormats {

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
