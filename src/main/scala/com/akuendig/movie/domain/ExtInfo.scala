package com.akuendig.movie.domain

import org.msgpack.annotation.{Message, Index}
import scala.reflect.runtime.universe._


@Message final case class ExtInfo(
  @Index(1) var id: String,
  @Index(2) var tpe: String,
  @Index(3) var title: String,
  @Index(4) var linkHref: Option[String] = None,
  @Index(5) var uris: Set[String] = Set.empty,
  @Index(6) var numRatings: Option[Int] = None,
  @Index(7) var rating: Option[Float] = None
) extends HasTypeTag {
  def this() = this("", "", "")

  lazy val getTypeTag: TypeTag[_] = typeTag[ExtInfo]
}
