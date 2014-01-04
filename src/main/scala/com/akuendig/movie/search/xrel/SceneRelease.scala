package com.akuendig.movie.search.xrel

import com.akuendig.movie.domain.Release


final case class SceneRelease(
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

      sizeInfo = size.map(_.toDomain),
      pubTime = time,
      extInfo = extInfo.map(_.toDomain),
      groupInfo = groupName.map(n => Group(id = "", name = Some(n)).toDomain)
    )
}
