package com.akuendig.movie.api

import com.akuendig.movie.core.{CoreActors, Core}
import akka.actor.Props
import spray.routing.RouteConcatenation
import com.akuendig.movie.{StaticResources, RoutedHttpService}

/**
 * The REST API layer. It exposes the REST services, but does not provide any
 * web server interface.<br/>
 * Notice that it requires to be mixed in with ``core.CoreActors``, which provides access
 * to the top-level actors that make up the system.
 */
trait ApiRoutes extends RouteConcatenation {
  this: CoreActors with Core =>

  private implicit val _ = system.dispatcher

  val routes =
    new MovieSearchService(directory).route ~
    new StaticResources().route

  val rootService = system.actorOf(Props(new RoutedHttpService(routes)))

}


