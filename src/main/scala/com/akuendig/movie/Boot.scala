package com.akuendig.movie


import com.akuendig.movie.core.{CoreActors, BootedCore}
import com.akuendig.movie.api.{Web, ApiRoutes}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext


object Boot extends App with BootedCore with CoreActors with ApiRoutes with Web {
  private implicit val _ec: ExecutionContext = system.dispatcher

  //  system.scheduler.schedule(1.second, 15.seconds, directoryRef, MovieDirectoryActor.MovieDirectoryPing)
}
