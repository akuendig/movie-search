package com.akuendig.movie.domain

import org.msgpack.annotation.{Message, Index}
import java.lang.{Integer => Int, Long, Float, Double}
import scala.reflect.runtime.universe._


@Message final case class PagedReleases(
  @Index(1) var page: Int,
  @Index(2) var totalPages: Int,
  @Index(3) var perPage: Int,
  @Index(4) var releases: Set[Release] = Set.empty
) extends HasTypeTag {
  def this() = this(0, 0, 0)

  lazy val getTypeTag: TypeTag[_] = typeTag[PagedReleases]
}
