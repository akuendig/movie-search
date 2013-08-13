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

  time: Option[Long],
  groupName: Option[String],

  audioType: Option[String],
  videoType: Option[String],

  tvSeason: Option[Int],
  tvEpisode: Option[Int],

  size: Option[Size],
  extInfo: Option[ExtInfo],
  flags: Option[Flags]
) {
  def toRelease: Release =
    Release(
      id = id,
      dirname = Some(dirname),
      linkHref = Some(linkHref),

      audioType = audioType,
      videoType = videoType,

      tvSeason = tvSeason,
      tvEpisode = tvEpisode,

      sizeInfo = size,
      pubTime = time,
      extInfo = extInfo,
      groupInfo = groupName.map[Group](n => Group(id = "", name = Some(n)))
    )
}
