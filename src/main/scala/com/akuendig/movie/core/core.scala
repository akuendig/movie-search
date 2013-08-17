package com.akuendig.movie.core

import akka.actor.{ActorRef, Props, ActorSystem}
import org.eligosource.eventsourced.core._
import com.akuendig.movie.search._
import scala.concurrent.ExecutionContext
import scala.concurrent.stm.Ref
import com.akuendig.movie.domain.Release

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

  def journal: ActorRef

  private implicit val _ec: ExecutionContext = system.dispatcher

  // Create an event-sourcing extension
  val extension = EventsourcingExtension(system, journal)

  // Create the movie directory. Only the DirectoryActor is allowed to write
  val movieDirectory: Ref[Map[String, Release]] = Ref(Map.empty[String, Release])

  // Create the querying actor communicating with the different external services
  val queryRef: ActorRef = system.actorOf(Props(new MovieQueryActor(new XrelQueryServiceImpl with SpraySendReceive)))

  // Create the actor responsible for updating the movie directory
  val directoryRef: ActorRef = extension.processorOf(Props(new MovieDirectoryActor(queryRef, movieDirectory) with Receiver with Eventsourced {
    val id = 1
  }))

  val directoryService = new MovieDirectoryService(movieDirectory)
}
