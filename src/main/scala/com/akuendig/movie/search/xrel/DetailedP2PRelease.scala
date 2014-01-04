package com.akuendig.movie.search.xrel

import com.akuendig.movie.domain.Release

case class DetailedP2PRelease(
  id: String,
  dirname: String,
  linkHref: String,

  mainLang: Option[String],
  pubTime: Option[Long],
  postTime: Option[Long],
  sizeMB: Option[Int],

  numRatings: Option[Int] = None,
  tvSeason: Option[Int] = None,
  tvEpisode: Option[Int] = None,

  group: Option[Group],
  extInfo: Option[ExtInfo],
  category: Option[Category]
) {
  def toRelease: Release =
    Release(
      id = id,
      dirname = Some(dirname),
      linkHref = Some(linkHref),

      mainLang = mainLang,
      pubTime = pubTime,
      postTime = postTime,
      sizeInfo = sizeMB.map(Size(_, Some("MB")).toDomain),

      numRatings = numRatings,
      tvSeason = tvSeason,
      tvEpisode = tvEpisode,

      groupInfo = group.map(_.toDomain),
      category = category.map(_.toDomain),
      extInfo = extInfo.map(_.toDomain)
    )
}
