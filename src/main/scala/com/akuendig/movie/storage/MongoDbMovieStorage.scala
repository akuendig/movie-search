package com.akuendig.movie.storage

import com.akuendig.movie.domain.{BsonFormats, Release}
import scala.concurrent.Future
import akka.actor.ActorSystem
import com.akuendig.movie.core.MongoDbExtension
import reactivemongo.api.indexes.{IndexType, Index}
import play.api.libs.iteratee.Enumerator
import scala.util.{Failure, Success}


class MongoDbMovieStorage(implicit system: ActorSystem) extends MovieStorage with BsonFormats {

  private val log = akka.event.Logging(system, getClass)
  private val mongoDb = MongoDbExtension(system)

  private implicit val _ec = system.dispatcher

  def init(): Future[Unit] = for {
    _ <- mongoDb.releasesCollection.create()
    _ <- mongoDb.releasesCollection.indexesManager.ensure(Index(
      key = Seq("id" -> IndexType.Hashed),
      unique = true,
      dropDups = true
    ))
  } yield ()

  def get(skip: Int, take: Int): Future[Traversable[Release]] = ???

  def put(releases: Traversable[Release]): Future[Int] = {
    val hash = releases.map(_.id.hashCode).sum

    mongoDb.releasesCollection.bulkInsert(Enumerator.enumerate(releases)).andThen {
      case Success(count) =>
        log.info("successfully inserted {}, count: {}", hash, count)
      case Failure(t) =>
        log.error(t, "failure inserting {}", hash)
    }
  }
}
