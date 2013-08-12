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

  size: Option[Size],
  extInfo: ExtInfo,
  flags: Flags
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
      pubTime = Some(time),
      extInfo = Some(extInfo),
      groupInfo = Some(Group(id = "", name = Some(groupName)))
    )
}
