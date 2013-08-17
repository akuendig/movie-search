package com.akuendig.movie.domain

import org.msgpack.annotation.{Message, Index}
import java.lang.{Integer => Int, Long, Float, Double}
import scala.reflect.runtime.universe._


@Message final case class QuerySceneReleasesResponse(
  @Index(1) var query: QuerySceneReleases = QuerySceneReleases(),
  @Index(2) var result: Option[PagedReleases] = None
) extends HasTypeTag {
  def this() = this(null)

  lazy val getTypeTag: TypeTag[_] = typeTag[QuerySceneReleasesResponse]
}
