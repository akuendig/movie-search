package com.akuendig.movie

import spray.routing.HttpService

// Trait for serving static resources
// Sends 404 for 'favicon.icon' requests and serves static resources in 'bootstrap' folder.
trait StaticResources extends HttpService {

   val staticResources =
     get {
       pathPrefix("") {
         getFromResourceDirectory("webapp/")
       } ~ path("") {
         getFromResource("webapp/index.html")
       }
     }
 }
