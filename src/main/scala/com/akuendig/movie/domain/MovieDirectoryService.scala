package com.akuendig.movie.domain

import akka.actor.ActorSystem
import com.akuendig.movie.core.MongoDbExtension
import reactivemongo.bson.BSONDocument
import scala.concurrent.Future
import reactivemongo.api.QueryOpts


class MovieDirectoryService(implicit system: ActorSystem) {

  import com.akuendig.movie.domain.Implicits._
  import scala.concurrent.ExecutionContext.Implicits.global

  private val mongoDb = MongoDbExtension(system)

  def getMovies(skip: Int, take: Int): Future[List[Release]] =
    mongoDb.releasesCollection
      .find(BSONDocument())
      .options(QueryOpts(skipN = skip))
      .cursor[Release]
      .toList(take)
}
