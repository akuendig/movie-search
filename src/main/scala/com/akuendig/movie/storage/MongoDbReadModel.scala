package com.akuendig.movie.storage

import akka.actor.{ActorLogging, Actor}
import com.akuendig.movie.domain._
import org.eligosource.eventsourced.core.Receiver
import com.akuendig.movie.core.MongoDbExtension
import reactivemongo.api.indexes.{IndexType, Index}
import scala.concurrent.Await
import play.api.libs.iteratee.Enumerator
import scala.util.{Failure, Success}
import scala.concurrent.duration._


object MongoDbReadModel {

  sealed trait MongoDbReadModelMessage

  case class StoreReleases(release: Traversable[Release]) extends MongoDbReadModelMessage

}

class MongoDbReadModel() extends Actor with ActorLogging {
  this: Receiver =>

  import MongoDbReadModel._

  private val mongoDb = MongoDbExtension(context.system)

  private implicit val _ec = context.dispatcher

  override def preStart() {
    val setup = for {
      _ <- mongoDb.releasesCollection.create()
      _ <- mongoDb.releasesCollection.indexesManager.ensure(Index(
        key = Seq("id" -> IndexType.Hashed),
        unique = true,
        dropDups = true
      ))
    } yield ()

    Await.ready(setup, 5.seconds)
  }

  override def receive: Receive = {
    case StoreReleases(releases) =>
      val msg = message
      val hash = releases.map(_.id.hashCode).sum

      mongoDb.releasesCollection.bulkInsert(Enumerator.enumerate(releases)).onComplete {
        case Success(count) =>
          log.info("successfully inserted {}, count: {}", hash, count)
          msg.confirm(pos = true)
        case Failure(t) =>
          log.error(t, "failure inserting {}", hash)
          msg.confirm(pos = false)
      }
  }
}
