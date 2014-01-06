package com.akuendig.movie.storage

import akka.actor.ActorSystem
import com.akuendig.movie.domain.Release
import com.akuendig.movie.core.StorageConfigExtension
import java.util.UUID
import java.nio.file.{Files, StandardOpenOption}
import net.java.truevfs.access._
import resource._
import scala.concurrent.Future
import net.java.truevfs.kernel.spec.FsSyncOptions

class ArchiveMovieStorage(implicit system: ActorSystem) extends MovieStorage {

  import org.json4s._
  import org.json4s.jackson.Serialization
  import org.json4s.jackson.Serialization.write

  implicit val _form = Serialization.formats(NoTypeHints)
  implicit val _ec   = system.dispatcher

  private val storageConfig = StorageConfigExtension(system)
  private val archiveLock   = storageConfig.archiveLock
  private val archivePath   = new TPath(storageConfig.archive)

  def get(skip: Int, take: Int): Future[Traversable[Release]] = ???

  def put(movies: Traversable[Release]): Future[Int] =
    Future {
      archiveLock.synchronized {
        val fileName = UUID.randomUUID().toString
        val filePath = archivePath.resolve(fileName)

        println(s"Storing ${movies.size} releases into archive $filePath.")

        for {
          writer <- managed(Files.newOutputStream(filePath, StandardOpenOption.CREATE_NEW))
        } write(movies, writer)

        TVFS.sync(FsSyncOptions.SYNC)

        movies.size
      }
    }
}
