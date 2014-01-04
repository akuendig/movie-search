package com.akuendig.movie.domain

import org.msgpack.annotation.{Message, Index}
import scala.reflect.runtime.universe._


@Message final case class PagedReleases(
  @Index(1) var page: Int = 0,
  @Index(2) var totalPages: Int = 0,
  @Index(3) var perPage: Int = 0,
  @Index(4) var releases: Set[Release] = Set.empty
) extends HasTypeTag {
  def this() = this(0)

  lazy val getTypeTag: TypeTag[_] = typeTag[PagedReleases]
}
