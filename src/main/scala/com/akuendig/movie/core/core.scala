package com.akuendig.movie.core

import akka.actor.{ActorRef, Props, ActorSystem}
import com.akuendig.movie.ChannelIds
import org.eligosource.eventsourced.journal.leveldb.LeveldbJournalProps
import java.io.File
import org.eligosource.eventsourced.core._
import com.akuendig.movie.search._
import scala.concurrent.{Future, ExecutionContext}
import spray.http.{HttpResponse, HttpRequest}

/**
 * Core is type containing the ``system: ActorSystem`` member. This enables us to use it in our
 * apps as well as in our tests.
 */
trait Core {

  implicit def system: ActorSystem

}

/**
 * This trait implements ``Core`` by starting the required ``ActorSystem`` and registering the
 * termination handler to stop the system when the JVM exits.
 */
trait BootedCore extends Core {

  /**
   * Construct the ActorSystem we will use in our application
   */
  implicit lazy val system = ActorSystem("movies")

  /**
   * Ensure that the constructed ActorSystem is shut down when the JVM shuts down
   */
  sys.addShutdownHook(system.shutdown())

}

/**
 * This trait contains the actors that make up our application; it can be mixed in with
 * ``BootedCore`` for running code or ``TestKit`` for unit and integration tests.
 */
trait CoreActors {
  this: Core =>

  private val _system: ActorSystem = system
  private implicit val _ec: ExecutionContext = system.dispatcher

  trait CurrentSystem extends Core {
    lazy val system = _system
  }

  // create a journal
  val journal: ActorRef = LeveldbJournalProps(new File("eventlog/only-scene"), native = false).createJournal

  // create an event-sourcing extension
  val extension = EventsourcingExtension(system, journal)

  val query: ActorRef = system.actorOf(Props(new MovieQueryActor(new XrelQueryServiceImpl() with SpraySendReceive with CurrentSystem) with Receiver))

  val queryChannel: ActorRef = extension.channelOf(DefaultChannelProps(ChannelIds.MovieQueryChannel, query))

  val directory: ActorRef = extension.processorOf(Props(new MovieDirectoryActor(queryChannel) with Receiver with Confirm with Eventsourced {
    val id = 1
  }))
}
