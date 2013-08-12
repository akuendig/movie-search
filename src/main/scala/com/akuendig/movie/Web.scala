package com.akuendig.movie

import core.{CoreActors, Core}
import akka.io.IO
import spray.can.Http
import com.akuendig.movie.api.ApiRoutes

/**
 * Provides the web server (spray-can) for the REST api in ``ApiRoutes``, using the actor system
 * defined in ``Core``.
 *
 * You may sometimes wish to construct separate ``ActorSystem`` for the web server machinery.
 * However, for this simple application, we shall use the same ``ActorSystem`` for the
 * entire application.
 *
 * Benefits of separate ``ActorSystem`` include the ability to use completely different
 * configuration, especially when it comes to the threading model.
 */
trait Web {
  this: ApiRoutes with CoreActors with Core =>

  IO(Http)(system) ! Http.Bind(rootService, "localhost", port = 9000)

}
