package com.akuendig.movie.domain

import org.msgpack.annotation.{Message, Index}
import java.lang.{Integer => Int, Long, Float, Double}
import scala.reflect.runtime.universe._


@Message final case class QuerySceneReleases(
  @Index(1) var year: Int = 0,
  @Index(2) var month: Int = 0,
  @Index(3) var page: Int = 0
) extends HasTypeTag {
  def this() = this(0)

  lazy val getTypeTag: TypeTag[_] = typeTag[QuerySceneReleases]
}
