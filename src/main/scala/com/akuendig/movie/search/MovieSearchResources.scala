package com.akuendig.movie.search

import spray.routing.HttpService

trait MovieSearchResources extends HttpService {

  val dynamicResources =
    path("order" / IntNumber) {
      id =>
        get {
          complete {
            "Received GET request for order " + id
          }
        } ~
          put {
            complete {
              "Received PUT request for order " + id
            }
          }
    }
}
