package com.akuendig.movie.core

import akka.actor.{ActorRef, Props, ActorSystem}
import org.eligosource.eventsourced.core._
import com.akuendig.movie.search._
import akka.contrib.throttle.Throttler._
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext
import akka.contrib.throttle.TimerBasedThrottler
import com.akuendig.movie.http.SpraySendReceive
import com.akuendig.movie.storage.{MovieDirectoryActor, MongoDbReadModel}

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
  val queryRef: ActorRef = system.actorOf(Props(new MovieQueryActor(new XrelQueryServiceImpl with SpraySendReceive)))

  val readModelRef: ActorRef = system.actorOf(Props(new MongoDbReadModel() with Receiver))
  val readModelThrottler = system.actorOf(Props(classOf[TimerBasedThrottler], 10.msgsPerSecond))
  // Set the target
  readModelThrottler ! SetTarget(Some(readModelRef))

  // Create the actor responsible for updating the movie directory
  val directoryRef: ActorRef = system.actorOf(Props(new MovieDirectoryActor(queryRef, readModelThrottler) {
    val id = 1
  }))
}
