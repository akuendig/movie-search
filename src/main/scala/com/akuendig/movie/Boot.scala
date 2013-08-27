package com.akuendig.movie


import com.akuendig.movie.core.{MsgPackSerializer, CoreActors, BootedCore}
import com.akuendig.movie.api.ApiRoutes
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext
import akka.actor.ActorRef
import org.eligosource.eventsourced.journal.leveldb.LeveldbJournalProps
import java.io.File
import org.eligosource.eventsourced.core.{SnapshotSaved, SnapshotRequest}
import akka.pattern.ask
import scala.util.{Failure, Success}
import akka.util.Timeout


object Boot extends App with BootedCore with CoreActors with ApiRoutes with Web {

  // create a journal
  lazy val journal: ActorRef = LeveldbJournalProps(
    dir = new File("eventlog/only-scene"),
    native = false,
    snapshotSerializer = new MsgPackSerializer()
  ).createJournal

  private implicit val _ec: ExecutionContext = system.dispatcher

  // recover registered processors by replaying journaled events
  //  extension.recover(extension.replayParams.allWithSnapshot)

  extension.recover()

  def takeSnapshot {
    implicit val timeout = Timeout(1.minute)

    (directoryRef ? SnapshotRequest).mapTo[SnapshotSaved].onComplete {
      case Success(saved) =>
        system.log.info("Saved snapshot {}", saved)
      case Failure(t) =>
        system.log.error(t, "Snapshot failed for directoryActor")
    }
  }

//  system.scheduler.schedule(1.minute, 30.minutes)(takeSnapshot)
  //  system.scheduler.schedule(1.second, 15.seconds, directoryRef, MovieDirectoryActor.MovieDirectoryPing)
}
