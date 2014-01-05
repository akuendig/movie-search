package com.akuendig.movie.storage

import akka.actor.{ActorLogging, Actor}
import com.akuendig.movie.domain._


object ReadModel {

  sealed trait ReadModelMessage

  final case class StoreReleases(release: Traversable[Release]) extends ReadModelMessage

}

class ReadModel(storage: MovieStorage) extends Actor with ActorLogging {

  import ReadModel._

  override def receive: Receive = {
    case StoreReleases(releases) => storage.put(releases)
  }
}
