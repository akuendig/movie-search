package com.akuendig.movie.search

import scala.concurrent.Future
import org.json4s.DefaultFormats

object XrelQueryModels {

  case class Pagination(
    currentPage: Int,
    perPage: Int,
    totalPages: Int
  )

  case class PagedSceneReleases(
    totalCount: Int,
    pagination: Pagination,
    list: Seq[SceneRelease]
  )

  case class PagedP2PReleases(
    totalCount: Int,
    pagination: Pagination,
    list: Seq[P2PRelease]
  )

  case class Flags(
    english: Option[Boolean]
  )

  case class ExtInfo(
    id: String,
    tpe: String, // actually 'type'
    title: String,
    linkHref: String,
    rating: Option[Float] = None,
    uris: Seq[String] = Seq.empty,
    numRatings: Option[Int] = None
  ) extends Serializable

  case class Size(
    number: Int,
    unit: String
  ) extends Serializable

  case class SceneRelease(
    id: String,
    dirname: String,
    linkHref: String,

    time: Long,
    groupName: String,

    audioType: Option[String],
    videoType: Option[String],

    tvSeason: Option[Int],
    tvEpisode: Option[Int],

    size: Size,
    extInfo: ExtInfo,
    flags: Flags
  ) {
    def toRelease: Release =
      Release(
        id = id,
        dirname = dirname,
        linkHref = linkHref,

        audioType = audioType,
        videoType = videoType,

        tvSeason = tvSeason,
        tvEpisode = tvEpisode,

        size = size,
        pubTime = time,
        extInfo = extInfo,
        group = Group(id = "", name = groupName)
      )
  }


  case class DetailedSceneRelease(
    id: String,
    dirname: String,
    linkHref: String,

    time: Long,
    groupName: String,

    audioType: Option[String],
    videoType: Option[String],

    tvSeason: Option[Int] = None,
    tvEpisode: Option[Int] = None,

    numRatings: Int,
    videoRating: Float,
    audioRating: Float,

    size: Size,
    extInfo: ExtInfo,
    flags: Flags
  ) {
    def toRelease: Release =
      Release(
        id = id,
        dirname = dirname,
        linkHref = linkHref,

        audioType = audioType,
        videoType = videoType,

        tvSeason = tvSeason,
        tvEpisode = tvEpisode,

        numRatings = Some(numRatings),
        audioRating = Some(audioRating),
        videoRating = Some(videoRating),

        size = size,
        pubTime = time,
        extInfo = extInfo,
        group = Group(id = "", name = groupName)
      )
  }


  case class Category(
    id: String,
    metaCat: String,
    subCat: String
  ) extends Serializable

  case class Group(
    id: String,
    name: String
  ) extends Serializable

  case class P2PRelease(
    id: String,
    dirname: String,
    linkHref: String,

    mainLang: String,
    pubTime: Long,
    sizeMB: Int,

    tvSeason: Option[Int] = None,
    tvEpisode: Option[Int] = None,

    group: Group,
    category: Category,
    extInfo: ExtInfo
  ) {
    def toRelease: Release =
      Release(
        id = id,
        dirname = dirname,
        linkHref = linkHref,

        mainLang = Some(mainLang),
        pubTime = pubTime,
        size = Size(sizeMB, "MB"),

        tvSeason = tvSeason,
        tvEpisode = tvEpisode,

        group = group,
        category = Some(category),
        extInfo = extInfo
      )
  }

  case class DetailedP2PRelease(
    id: String,
    dirname: String,
    linkHref: String,

    mainLang: String,
    pubTime: Long,
    postTime: Long,
    sizeMB: Int,

    numRatings: Option[Int] = None,
    tvSeason: Option[Int] = None,
    tvEpisode: Option[Int] = None,

    group: Group,
    category: Category,
    extInfo: ExtInfo
  ) {
    def toRelease: Release =
      Release(
        id = id,
        dirname = dirname,
        linkHref = linkHref,

        mainLang = Some(mainLang),
        pubTime = pubTime,
        postTime = Some(postTime),
        size = Size(sizeMB, "MB"),

        numRatings = numRatings,
        tvSeason = tvSeason,
        tvEpisode = tvEpisode,

        group = group,
        category = Some(category),
        extInfo = extInfo
      )
  }

  case class RateLimit(
    remainingCalls: Int,
    resetTimeU: Long,
    resetTime: String
  )

  case class Release(
    id: String,
    dirname: String,
    linkHref: String,

    pubTime: Long,
    size: Size,

    group: Group,

    extInfo: ExtInfo,

    category: Option[Category] = None,
    mainLang: Option[String] = None,

    audioType: Option[String] = None,
    videoType: Option[String] = None,

    postTime: Option[Long] = None,
    tvSeason: Option[Int] = None,
    tvEpisode: Option[Int] = None,

    numRatings: Option[Int] = None,
    audioRating: Option[Float] = None,
    videoRating: Option[Float] = None
  ) extends Serializable

  case class PagedReleases(
    page: Int,
    totalPages: Int,
    perPage: Int,
    releases: Seq[Release]
  ) extends Serializable

  val xrelFormats = DefaultFormats.withCompanions(
    classOf[SceneRelease] -> this,
    classOf[Flags] -> this,
    classOf[ExtInfo] -> this,
    classOf[Size] -> this,
    classOf[SceneRelease] -> this,
    classOf[DetailedSceneRelease] -> this,
    classOf[Category] -> this,
    classOf[Group] -> this,
    classOf[P2PRelease] -> this,
    classOf[DetailedP2PRelease] -> this,
    classOf[RateLimit] -> this,
    classOf[Pagination] -> this,
    classOf[PagedSceneReleases] -> this,
    classOf[PagedP2PReleases] -> this
  )
}


trait XrelQueryComponent {

  import XrelQueryModels._

  def xrelQueryService: XrelQueryService

  trait XrelQueryService {
    def fetchSceneRelease(page: Int, year: Int, month: Int): Future[PagedSceneReleases]

    def fetchDetailedSceneRelease(id: String): Future[DetailedSceneRelease]

    def fetchP2PCategories(): Future[Seq[Category]]

    def fetchP2PRelease(page: Int, catId: String): Future[PagedP2PReleases]

    def fetchDetailedP2PRelease(id: String): Future[DetailedP2PRelease]

    def fetchRateLimit(): Future[RateLimit]
  }

}
