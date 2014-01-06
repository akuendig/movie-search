package com.akuendig.movie.storage

import akka.actor.{ActorLogging, Actor}
import akka.pattern._
import com.akuendig.movie.domain._
import scala.concurrent.ExecutionContext


object ReadModel {

  sealed trait ReadModelMessage

  final case class StoreReleases(release: Traversable[Release]) extends ReadModelMessage

  final case class StoreReleasesComplete(release: Traversable[Release]) extends ReadModelMessage

}

class ReadModel(storage: MovieStorage) extends Actor with ActorLogging {

  import ReadModel._

  private implicit val _ec: ExecutionContext = context.system.dispatcher

  override def receive: Receive = {
    case StoreReleases(releases) =>
      storage.
        put(releases).
        map(_ => StoreReleasesComplete(releases)).
        pipeTo(sender)
  }
}
