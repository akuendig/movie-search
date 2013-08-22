package com.akuendig.movie


import com.akuendig.movie.core.{KnowsSerializer, MsgPackSerializer, CoreActors, BootedCore}
import com.akuendig.movie.api.ApiRoutes
import com.akuendig.movie.search.MovieDirectoryActor
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext
import akka.actor.ActorRef
import org.eligosource.eventsourced.journal.leveldb.LeveldbJournalProps
import java.io.File
import org.eligosource.eventsourced.core.{SnapshotSaved, SnapshotRequest}
import akka.pattern.ask
import scala.util.{Failure, Success}
import akka.util.Timeout
import akka.serialization.Serializer
import org.eligosource.eventsourced.journal.common.serialization.SnapshotSerializer


object Boot extends App with BootedCore with CoreActors with ApiRoutes with Web {

  // create a journal
  lazy val journal: ActorRef = LeveldbJournalProps(
    dir = new File("eventlog/only-scene"),
    native = false,
    snapshotSerializer = new MsgPackSerializer(),
    snapshotLoadTimeout = 5.minutes,
    snapshotSaveTimeout = 5.minutes
  ).createJournal

  private implicit val _ec: ExecutionContext = system.dispatcher

  // recover registered processors by replaying journaled events
  extension.recover(extension.replayParams.allWithSnapshot, 5.minutes)

  //  extension.recover()

  def takeSnapshot {
    implicit val timeout = Timeout(5.minutes)

    (directoryRef ? SnapshotRequest).mapTo[SnapshotSaved].onComplete {
      case Success(saved) =>
        system.log.info("Saved snapshot {}", saved)
      case Failure(t) =>
        system.log.error(t, "Snapshot failed for directoryActor")
    }
  }

  system.scheduler.schedule(30.minute, 30.minutes)(takeSnapshot)
//  system.scheduler.schedule(1.second, 15.seconds, directoryRef, MovieDirectoryActor.MovieDirectoryPing)
}
