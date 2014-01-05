package com.akuendig.movie.core

import com.typesafe.config.{ConfigValueFactory, ConfigFactory, Config}
import akka.actor.{ExtendedActorSystem, ExtensionIdProvider, ExtensionId, Extension}
import java.io.File
import com.akuendig.movie.search.ScrapingState


class StorageConfigExtensionImpl(config: Config) extends Extension {
  private def stateFile: File = {
    val path = config.getString("storage.state")
    val file = new File(path)

    // Creates file, if it does not exist.
    file.createNewFile()

    file
  }

  private lazy val defaultConf = ConfigFactory.parseResources("state.conf")
  private lazy val conf = ConfigFactory.parseFile(stateFile).withFallback(defaultConf)

  def snapshotScene(state: ScrapingState) {
    conf.withValue("xrel.scene.year", ConfigValueFactory.fromAnyRef(state.year))
    conf.withValue("xrel.scene.month", ConfigValueFactory.fromAnyRef(state.month))
    conf.withValue("xrel.scene.page", ConfigValueFactory.fromAnyRef(state.page))
    conf.withValue("xrel.scene.totalPages", ConfigValueFactory.fromAnyRef(state.totalPages))
  }

  def scene: ScrapingState =
    ScrapingState(
      conf.getInt("xrel.scene.year"),
      conf.getInt("xrel.scene.month"),
      conf.getInt("xrel.scene.page"),
      conf.getInt("xrel.scene.totalPages")
    )
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
