package com.akuendig.movie.core

import akka.actor.{ActorRef, Props, ActorSystem}
import com.akuendig.movie.search._
import scala.concurrent.ExecutionContext
import com.akuendig.movie.http.SpraySendReceive
import com.akuendig.movie.storage.ArchiveReadModel

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

  private implicit val _ec: ExecutionContext = system.dispatcher

  // Create the querying actor communicating with the different external services
  val queryRef: ActorRef = system.actorOf(Props(
    classOf[MovieQueryActor],
    new XrelQueryServiceImpl with SpraySendReceive
  ),
    "xrel-query"
  )

  val readModelRef: ActorRef = system.actorOf(Props[ArchiveReadModel], "read-model")

  // Create the actor responsible for updating the movie directory
  val directoryRef: ActorRef = system.actorOf(Props(
    classOf[ScrapeCoordinator],
    queryRef,
    readModelRef
  ),
    "scrape-coordinator")
}
