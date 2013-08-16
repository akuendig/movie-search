package com.akuendig.movie.domain

import org.msgpack.annotation.{Message, Index}
import scala.reflect.runtime.universe._


trait HasTypeTag {
  def getTypeTag: TypeTag[_]
}

@Message class Size(
  @Index(1) var number: Int,
  @Index(2) var unit: Option[String] = None
) extends HasTypeTag {
  def this() = this(0)

  lazy val getTypeTag: TypeTag[_] = typeTag[Size]
}

@Message class Category(
  @Index(1) var id: String,
  @Index(2) var metaCat: Option[String] = None,
  @Index(3) var subCat: Option[String] = None
) extends HasTypeTag {
  def this() = this("")

  lazy val getTypeTag: TypeTag[_] = typeTag[Category]
}


@Message class ExtInfo(
  @Index(1) var id: String,
  @Index(2) var tpe: String,
  @Index(3) var title: String,
  @Index(4) var linkHref: Option[String] = None,
  @Index(5) var uris: Seq[String] = Seq.empty,
  @Index(6) var numRatings: Option[Int] = None,
  @Index(7) var rating: Option[Float] = None
) extends HasTypeTag {
  def this() = this("", "", "")

  lazy val getTypeTag: TypeTag[_] = typeTag[ExtInfo]
}

@Message class Flags(
  @Index(1) var english: Option[Boolean] = None
) extends HasTypeTag {
  def this() = this(None)

  lazy val getTypeTag: TypeTag[_] = typeTag[Flags]
}

@Message class Group(
  @Index(1) var id: String,
  @Index(2) var name: Option[String] = None
) extends HasTypeTag {
  def this() = this("")

  lazy val getTypeTag: TypeTag[_] = typeTag[Group]
}

@Message class Release(
  @Index(1) var id: String,

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

@Message class PagedReleases(
  @Index(1) var page: Int,
  @Index(2) var totalPages: Int,
  @Index(3) var perPage: Int,
  @Index(4) var releases: Seq[Release] = Seq.empty
) extends HasTypeTag {
  def this() = this(0, 0, 0)

  lazy val getTypeTag: TypeTag[_] = typeTag[PagedReleases]
}

@Message class QuerySceneReleases(
  @Index(1) var year: Int,
  @Index(2) var month: Int,
  @Index(3) var page: Int
) extends HasTypeTag {
  def this() = this(0, 0, 0)

  lazy val getTypeTag: TypeTag[_] = typeTag[QuerySceneReleases]
}

@Message class QuerySceneReleasesResponse(
  @Index(1) var query: QuerySceneReleases,
  @Index(2) var result: Option[PagedReleases] = None
) extends HasTypeTag {
  def this() = this(null)

  lazy val getTypeTag: TypeTag[_] = typeTag[QuerySceneReleasesResponse]
}

@Message class MovieDirectorySnapshot(
  @Index(1) var year: Int,
  @Index(2) var month: Int,
  @Index(3) var page: Int,
  @Index(4) var totalPages: Int,
  @Index(5) var releases: Seq[Release] = Seq.empty
) extends HasTypeTag {
  def this() = this(0, 0, 0, 0)

  lazy val getTypeTag: TypeTag[_] = typeTag[MovieDirectorySnapshot]
}

