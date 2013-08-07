package com.akuendig.movie.search.xrel

import com.akuendig.movie.search.domain._
import com.akuendig.movie.search.domain.Size
import com.akuendig.movie.search.domain.Flags
import com.akuendig.movie.search.domain.Release
import com.akuendig.movie.search.domain.ExtInfo


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
