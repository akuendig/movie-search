package com.akuendig.movie.storage

import akka.actor.Actor
import com.akuendig.movie.domain.Release
import com.akuendig.movie.core.StorageConfigExtension
import java.util.UUID
import java.nio.file.{Path, Files, StandardOpenOption}
import net.java.truevfs.access._
import net.java.truevfs.kernel.spec.FsSyncOptions
import resource.managed
import scala.concurrent.Future
import scala.collection.JavaConversions._


class ArchiveReadModel() extends Actor {

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
    loadIndex()
  }

  private def read(file: Path): Seq[Release] = {
    import org.json4s.jackson.JsonMethods._

    parse(FileInput(file.toFile)).extract[Seq[Release]]
  }

  private def write(releases: Traversable[Release]): Path = {
    val fileName = UUID.randomUUID().toString
    val filePath = archivePath.resolve(fileName)

    for {
      writer <- managed(Files.newOutputStream(filePath, StandardOpenOption.CREATE_NEW))
    } Serialization.write(releases, writer)

    filePath
  }

  private def loadIndex() {
    var indexBuilder = Map.newBuilder[String, Path]

    for {
      dir <- managed(Files.newDirectoryStream(archivePath, "*.json"))
      file <- dir
      id <- read(file).map(_.id)
    } indexBuilder += id -> file

    index = indexBuilder.result()
  }

  private def addIndex(releases: Traversable[Release], file: Path) {
    index ++= releases.map(_.id -> file)
  }

  private def notIndexed(releases: Traversable[Release]): Traversable[Release] = {
    releases.filter(r => index.contains(r.id))
  }

  def get(skip: Int, take: Int): Future[Traversable[Release]] = ???

  def receive: Actor.Receive = {
    case StoreReleases(releases) =>
      val filtered = notIndexed(releases)
      val file = write(filtered)

      addIndex(filtered, file)

      sender ! StoreReleasesComplete(releases)
    case GetPaged(skip, take) =>

  }
}
