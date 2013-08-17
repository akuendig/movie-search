package com.akuendig.movie.search.xrel

import com.akuendig.movie.domain._


case class P2PRelease(
  id: String,
  dirname: String,
  linkHref: String,

  mainLang: Option[String],
  pubTime: Option[Long],
  sizeMB: Option[Int],

  tvSeason: Option[Int],
  tvEpisode: Option[Int],

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
      sizeInfo = sizeMB.map(Size(_, Some("MB"))),

      tvSeason = tvSeason,
      tvEpisode = tvEpisode,

      groupInfo = group,
      category = category,
      extInfo = extInfo
    )
}
