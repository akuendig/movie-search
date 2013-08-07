package com.akuendig.movie


import com.akuendig.movie.core.{CoreActors, BootedCore}
import com.akuendig.movie.api.ApiRoutes
import com.akuendig.movie.search.MovieDirectoryActor
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext


object Boot extends App with BootedCore with CoreActors with ApiRoutes with Web {

  private implicit val _ec: ExecutionContext = system.dispatcher

  println("BOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOTING")

  // recover registered processors by replaying journaled events
  extension.recover()

  // send event message to processor (will be journaled)
  system.scheduler.scheduleOnce(1.second, directory, MovieDirectoryActor.MovieDirectoryPing)
}
