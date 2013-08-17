package com.akuendig.movie.domain

import org.msgpack.annotation.{Message, Index}
import java.lang.{Integer => Int, Long, Float, Double}
import scala.reflect.runtime.universe._


@Message final case class QuerySceneReleases(
  @Index(1) var year: Int,
  @Index(2) var month: Int,
  @Index(3) var page: Int
) extends HasTypeTag {
  def this() = this(0, 0, 0)

  lazy val getTypeTag: TypeTag[_] = typeTag[QuerySceneReleases]
}
