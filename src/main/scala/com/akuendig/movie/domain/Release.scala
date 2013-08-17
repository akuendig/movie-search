package com.akuendig.movie.domain

import org.msgpack.annotation.{Message, Index}
import java.lang.{Integer => Int, Long, Float, Double}
import scala.reflect.runtime.universe._


@Message final case class Release(
  @Index(1) var id: String = "",

  @Index(2) var dirname: Option[String] = None,
  @Index(3) var linkHref: Option[String] = None,

  @Index(4) var mainLang: Option[String] = None,
  @Index(5) var pubTime: Option[Long] = None,

  @Index(6) var sizeInfo: Option[Size] = None,
  @Index(7) var groupInfo: Option[Group] = None,
  @Index(8) var extInfo: Option[ExtInfo] = None,
  @Index(9) var category: Option[Category] = None,

  @Index(10) var audioType: Option[String] = None,
  @Index(11) var videoType: Option[String] = None,

  @Index(12) var postTime: Option[Long] = None,
  @Index(13) var tvSeason: Option[Int] = None,
  @Index(14) var tvEpisode: Option[Int] = None,

  @Index(15) var numRatings: Option[Int] = None,
  @Index(16) var audioRating: Option[Float] = None,
  @Index(17) var videoRating: Option[Float] = None
) extends HasTypeTag {
  def this() = this("")

  lazy val getTypeTag: TypeTag[_] = typeTag[Release]
}
