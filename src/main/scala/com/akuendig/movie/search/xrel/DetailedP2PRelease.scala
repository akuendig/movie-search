package com.akuendig.movie.search.xrel

import com.akuendig.movie.search.domain._
import com.akuendig.movie.search.domain.Group
import com.akuendig.movie.search.domain.Release
import com.akuendig.movie.search.domain.ExtInfo
import scala.Some
import com.akuendig.movie.search.domain.Category


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
      dirname = Some(dirname),
      linkHref = Some(linkHref),

      mainLang = Some(mainLang),
      pubTime = Some(pubTime),
      postTime = Some(postTime),
      sizeInfo = Some(Size(sizeMB, Some("MB"))),

      numRatings = numRatings,
      tvSeason = tvSeason,
      tvEpisode = tvEpisode,

      groupInfo = Some(group),
      category = Some(category),
      extInfo = Some(extInfo)
    )
}