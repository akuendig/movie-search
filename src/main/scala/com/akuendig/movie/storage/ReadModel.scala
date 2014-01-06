package com.akuendig.movie.storage

import akka.actor.{ActorLogging, Actor}
import akka.pattern._
import com.akuendig.movie.domain._
import scala.concurrent.ExecutionContext


object ReadModel {

  sealed trait ReadModelMessage

  final case class StoreReleases(release: Traversable[Release]) extends ReadModelMessage

  final case class StoreReleasesComplete(release: Traversable[Release]) extends ReadModelMessage

  final case class GetPaged(skip: Int, take: Int) extends ReadModelMessage

}
