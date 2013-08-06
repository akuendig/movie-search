package com.akuendig.movie.search

import scala.concurrent.Future
import org.json4s.DefaultFormats


trait XrelQueryComponent {

  def xrelQueryService: XrelQueryService

  trait XrelQueryService {
    def fetchSceneRelease(page: Int, year: Int, month: Int): Future[PagedSceneReleases]

    def fetchDetailedSceneRelease(id: String): Future[DetailedSceneRelease]

    def fetchP2PCategories(): Future[Seq[Category]]

    def fetchP2PRelease(page: Int, catId: String): Future[PagedP2PReleases]

    def fetchDetailedP2PRelease(id: String): Future[DetailedP2PRelease]

    def fetchRateLimit(): Future[RateLimit]
  }

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

  case class Flags()

  case class ExtInfo(
    id: String,
    tpe: String, // actually 'type'
    title: String,
    linkHref: String
    //    rating: Float
    // Uris       []String
    // NumRatings int
  )

  case class Size(
    number: Int,
    unit: String
  )

  case class SceneRelease(
    id: String,
    dirname: String,
    linkHref: String,

    audioType: String,
    videoType: String,

    groupName: String,
    time: Long,

    size: Size,

    extInfo: ExtInfo,

    flags: Flags
  )


  case class DetailedSceneRelease(
    id: String,
    dirname: String,
    linkHref: String,

    audioType: String,
    videoType: String,

    groupName: String,
    time: Long,

    size: Size,

    extInfo: ExtInfo,

    flags: Flags,

    videoRating: Float,
    audioRating: Float,
    numRatings: Int
  )


  case class Category(
    id: String,
    metaCat: String,
    subCat: String
  )

  case class Group(
    id: String,
    name: String
  )

  case class P2PRelease(
    id: String,
    dirname: String,
    linkHref: String,

    category: Category,

    mainLang: String,
    pubTime: Long,
    sizeMB: Int,

    group: Group,

    extInfo: ExtInfo
  )

  case class DetailedP2PRelease(
    id: String,
    dirname: String,
    linkHref: String,

    category: Category,

    mainLang: String,
    pubTime: Long,
    sizeMB: Int,

    group: Group,

    extInfo: ExtInfo,

    postTime: Long,
    numRatings: Int,
    tvSeason: Int,
    tvEpisode: Int
  )

  case class RateLimit(
    remainingCalls: Int,
    resetTimeU: Long,
    resetTime: String
  )

  val xrelFormats = DefaultFormats.withCompanions(
    classOf[Pagination] -> this,
    classOf[PagedSceneReleases] -> this,
    classOf[PagedP2PReleases] -> this,
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
    classOf[RateLimit] -> this
  )

}
