package com.akuendig.movie.storage

import com.akuendig.movie.domain.Release
import akka.actor.Actor


class NopReadModel extends Actor {

  import ReadModel._

  def receive: Actor.Receive = {
    case StoreReleases(releases) => sender ! StoreReleasesComplete(releases)
    case GetPaged(_, _) => sender ! Seq.empty[Release]
  }
}
