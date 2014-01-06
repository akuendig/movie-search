package com.akuendig.movie.api

import akka.actor.{ActorRef, ActorSystem}
import akka.pattern.ask
import akka.event.Logging
import akka.util.Timeout
import com.akuendig.movie.domain.Release
import com.akuendig.movie.storage.ReadModel.GetPaged
import org.json4s.DefaultFormats
import scala.concurrent.Future
import scala.concurrent.duration._
import spray.http.HttpMethods._
import spray.httpx.Json4sJacksonSupport
import spray.routing.Directives


class MovieSearchService(storage: ActorRef)(implicit val system: ActorSystem)
  extends Directives with CORSDirectives with Json4sJacksonSupport {

  val json4sJacksonFormats = DefaultFormats

  private val log    = Logging(system, getClass)
  private val origin = "http://localhost:9001"

  private implicit val _ec      = system.dispatcher
  private implicit val _timeout = Timeout(1.second)

  val route =
    (pathPrefix("api") & path("movies") & cqrsAllow(origin)(GET)) {
      get {
        parameters('skip.as[Int] ? 0, 'take.as[Int] ? 10) {
          (skip, take) =>
            val result = (storage ? GetPaged(skip, take)).asInstanceOf[Future[Seq[Release]]]

            complete(result)
        }
      }
    }
}
