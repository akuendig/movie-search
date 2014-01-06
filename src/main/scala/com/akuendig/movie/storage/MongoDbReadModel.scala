package com.akuendig.movie.storage

import akka.actor.{ActorLogging, Actor}
import akka.pattern.pipe
import com.akuendig.movie.core.MongoDbExtension
import com.akuendig.movie.domain.{BsonFormats, Release}
import play.api.libs.iteratee.Enumerator
import reactivemongo.api.indexes.{IndexType, Index}
import scala.concurrent.Await
import scala.concurrent.duration._
import scala.util.{Failure, Success}
import reactivemongo.bson.BSONDocument
import reactivemongo.api.QueryOpts


class MongoDbReadModel() extends Actor with ActorLogging with BsonFormats {

  import ReadModel._

  private val mongoDb = MongoDbExtension(context.system)

  private implicit val _ec = context.system.dispatcher

  override def preStart() {
    val action = for {
      _ <- mongoDb.releasesCollection.create()
      _ <- mongoDb.releasesCollection.indexesManager.ensure(Index(
        key = Seq("id" -> IndexType.Hashed),
        unique = true,
        dropDups = true
      ))
    } yield ()

    Await.ready(action, 10.seconds)
  }

  def receive: Actor.Receive = {
    case StoreReleases(releases) =>
      val hash = releases.map(_.id.hashCode).sum

      val result =
        mongoDb.
          releasesCollection.
          bulkInsert(Enumerator.enumerate(releases)).
          andThen({
            case Success(count) =>
              log.info("successfully inserted {}, count: {}", hash, count)
            case Failure(t)     =>
              log.error(t, "failure inserting {}", hash)
          }).
          map(_ => StoreReleasesComplete(releases))

      result.pipeTo(sender)

    case GetPaged(skip, take) =>
      mongoDb.
        releasesCollection.
        find(BSONDocument()).
        options(QueryOpts(skipN = skip)).
        cursor[Release].
        collect[Seq](take)
  }
}
