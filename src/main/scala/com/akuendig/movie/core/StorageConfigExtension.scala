package com.akuendig.movie.core

import akka.actor.{ExtendedActorSystem, ExtensionIdProvider, ExtensionId, Extension}
import com.akuendig.movie.search.ScrapingState
import com.typesafe.config.{ConfigValueFactory, ConfigFactory, Config}
import java.io.{FileOutputStream, File}
import resource.managed


class StorageConfigExtensionImpl(config: Config) extends Extension {
  private def stateFile: File = {
    val path = config.getString("storage.state")
    val file = new File(path)

    // Creates file, if it does not exist.
    file.createNewFile()

    file
  }

  private def defaultConf = ConfigFactory.parseResources("state.conf")

  private def conf = ConfigFactory.parseFile(stateFile).withFallback(defaultConf)

  def snapshotScene(state: ScrapingState) {
    val fullConfigString =
      conf.
        withValue("xrel.scene.year", ConfigValueFactory.fromAnyRef(state.year)).
        withValue("xrel.scene.month", ConfigValueFactory.fromAnyRef(state.month)).
        withValue("xrel.scene.page", ConfigValueFactory.fromAnyRef(state.page)).
        withValue("xrel.scene.totalPages", ConfigValueFactory.fromAnyRef(state.totalPages)).
        root.
        render()

    for {
      out <- managed(new FileOutputStream(stateFile, false))
    } out.write(fullConfigString.getBytes)
  }

  def snapshotMovies(pageCount: Int, totalPages: Int) {
    val fullConfigString =
      conf.
        withValue("xrel.scene.movies.pageCount", ConfigValueFactory.fromAnyRef(pageCount)).
        withValue("xrel.scene.movies.totalPages", ConfigValueFactory.fromAnyRef(totalPages)).
        root.
        render()

    for {
      out <- managed(new FileOutputStream(stateFile, false))
    } out.write(fullConfigString.getBytes)
  }

  def scene: ScrapingState =
    ScrapingState(
      conf.getInt("xrel.scene.year"),
      conf.getInt("xrel.scene.month"),
      conf.getInt("xrel.scene.page"),
      conf.getInt("xrel.scene.totalPages")
    )

  def movies: (Int, Int) = (
    conf.getInt("xrel.scene.movies.pageCount"),
    conf.getInt("xrel.scene.movies.totalPages")
    )

  def archive: String = config.getString("storage.archive")

  def archiveLock: Object = new Object
}

object StorageConfigExtension
  extends ExtensionId[StorageConfigExtensionImpl]
  with ExtensionIdProvider {
  //The lookup method is required by ExtensionIdProvider,
  // so we return ourselves here, this allows us
  // to configure our extension to be loaded when
  // the ActorSystem starts up
  override def lookup() = StorageConfigExtension

  //This method will be called by Akka
  // to instantiate our Extension
  override def createExtension(system: ExtendedActorSystem) = new StorageConfigExtensionImpl(system.settings.config)
}
