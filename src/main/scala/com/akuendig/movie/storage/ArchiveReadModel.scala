package com.akuendig.movie.storage

import akka.actor.{ActorLogging, Actor}
import com.akuendig.movie.domain.Release
import com.akuendig.movie.core.StorageConfigExtension
import java.util.UUID
import java.nio.file.{Path, Files, StandardOpenOption}
import net.java.truevfs.access._
import net.java.truevfs.kernel.spec.FsSyncOptions
import resource.managed
import scala.collection.JavaConversions._
import java.io.InputStreamReader


class ArchiveReadModel() extends Actor with ActorLogging {

  import org.json4s._
  import org.json4s.jackson.Serialization
  import ReadModel._

  implicit val _form = Serialization.formats(NoTypeHints)
  implicit val _ec   = context.system.dispatcher

  private val storageConfig = StorageConfigExtension(context.system)
  private val archivePath   = new TPath(storageConfig.archive)

  private var index = Map.empty[String, Path]

  override def postStop() {
    TVFS.sync(FsSyncOptions.SYNC)
  }

  override def preStart() {
    log.info("Starting up")
    loadIndex()
    log.info("Finished loading index with {} entries", index.size)
  }

  def read(file: Path): Seq[Release] = {
    managed(Files.newInputStream(file)).acquireAndGet {
      in => Serialization.read[Seq[Release]](new InputStreamReader(in))
    }
  }

  def write(releases: Traversable[Release]): Path = {
    val fileName = s"${UUID.randomUUID()}.json"
    val filePath = archivePath.resolve(fileName)

    for {
      writer <- managed(Files.newOutputStream(filePath, StandardOpenOption.CREATE_NEW))
    } Serialization.write(releases, writer)

    TVFS.sync(FsSyncOptions.SYNC)

    filePath
  }

  def loadIndex() {
    var indexBuilder = Map.newBuilder[String, Path]

    for {
      dir <- managed(Files.newDirectoryStream(archivePath))
      file <- dir

      id <- read(file).map(_.id)
    } indexBuilder += id -> file

    index = indexBuilder.result()
  }

  def addIndex(releases: Traversable[Release], file: Path) {
    index ++= releases.map(_.id -> file)
  }

  def notIndexed(releases: Traversable[Release]): Traversable[Release] = {
    releases.filterNot(r => index.contains(r.id))
  }

  def receive: Actor.Receive = {
    case StoreReleases(releases) =>
      val filtered = notIndexed(releases)

      if (filtered.size > 0) {
        val file = write(filtered)

        addIndex(filtered, file)
      }

      log.info("Stored {} releases", filtered.size)

      sender ! StoreReleasesComplete(releases)
    case GetPaged(skip, take) =>

  }
}
