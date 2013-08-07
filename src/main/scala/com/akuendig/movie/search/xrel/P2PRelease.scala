package com.akuendig.movie.search.xrel

import com.akuendig.movie.search.domain._
import com.akuendig.movie.search.domain.Group
import com.akuendig.movie.search.domain.Release
import com.akuendig.movie.search.domain.ExtInfo
import scala.Some
import com.akuendig.movie.search.domain.Category


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
