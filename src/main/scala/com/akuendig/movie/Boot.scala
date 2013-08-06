package com.akuendig.movie

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.io.IO
import spray.can.Http
import java.io.File

import org.eligosource.eventsourced.core._
import org.eligosource.eventsourced.journal.leveldb.LeveldbJournalProps
import com.akuendig.movie.search.{MovieDirectoryPing, MovieQueryActor, MovieDirectoryActor}
import scala.concurrent.duration._


object Boot extends App {

  // we need an ActorSystem to host our application in
  implicit val system = ActorSystem("movies")

  // the dispatcher provides an ExecutionContext
  import system.dispatcher

  // create and start our service actor
  val service = system.actorOf(Props[MovieServiceActor], "movie-service")

  // start a new HTTP server on port 8080 with our service actor as the handler
  IO(Http) ! Http.Bind(service, interface = "localhost", port = 8080)

  // create a journal
  val journal: ActorRef = LeveldbJournalProps(new File("eventlog/test"), native = false).createJournal

  // create an event-sourcing extension
  val extension = EventsourcingExtension(system, journal)

  val query : ActorRef = system.actorOf(Props(new MovieQueryActor() with Receiver))

  val queryChannel: ActorRef = extension.channelOf(DefaultChannelProps(ChannelIds.MovieQueryChannel, query))

  val directory: ActorRef = extension.processorOf(Props(new MovieDirectoryActor(queryChannel) with Receiver with Confirm with Eventsourced {
    val id = 1
  }))

  // recover registered processors by replaying journaled events
  extension.recover()

  // send event message to processor (will be journaled)
  system.scheduler.scheduleOnce(1.second, directory, MovieDirectoryPing)
}
