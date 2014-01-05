package com.akuendig.movie.core

import akka.actor._
import scala.collection.JavaConversions._
import com.typesafe.config.Config
import reactivemongo.api.{DefaultDB, MongoDriver}
import reactivemongo.api.collections.default.BSONCollection


class MongoDbExtensionImpl(config: Config) extends Extension {

  import scala.concurrent.ExecutionContext.Implicits.global

  private val uris: List[String] = config.getStringList("database.release-connections").to[List]

  private val driver = new MongoDriver()
  private val connection = driver.connection(uris)

  def db(name: String): DefaultDB = connection(name)

  def movieSearchDb: DefaultDB = db("movieSearch")

  def releasesCollection: BSONCollection = movieSearchDb("releases")
}

object MongoDbExtension
  extends ExtensionId[MongoDbExtensionImpl]
  with ExtensionIdProvider {
  //The lookup method is required by ExtensionIdProvider,
  // so we return ourselves here, this allows us
  // to configure our extension to be loaded when
  // the ActorSystem starts up
  override def lookup() = MongoDbExtension

  //This method will be called by Akka
  // to instantiate our Extension
  override def createExtension(system: ExtendedActorSystem) = new MongoDbExtensionImpl(system.settings.config)
}
