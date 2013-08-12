package com.akuendig.movie


import com.akuendig.movie.core.{CoreActors, BootedCore}
import com.akuendig.movie.api.ApiRoutes
import com.akuendig.movie.search.MovieDirectoryActor
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext
import akka.actor.ActorRef
import org.eligosource.eventsourced.journal.leveldb.LeveldbJournalProps
import java.io.File


object Boot extends App with BootedCore with CoreActors with ApiRoutes with Web {

  // create a journal
  lazy val journal: ActorRef = LeveldbJournalProps(new File("eventlog/only-scene"), native = false).createJournal

  private implicit val _ec: ExecutionContext = system.dispatcher

  // recover registered processors by replaying journaled events
  extension.recover()

  system.scheduler.schedule(1.second, 15.seconds, directoryRef, MovieDirectoryActor.MovieDirectoryPing)
}
