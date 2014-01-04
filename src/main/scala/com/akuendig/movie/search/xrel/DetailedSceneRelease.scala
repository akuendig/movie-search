package com.akuendig.movie.search.xrel

import com.akuendig.movie.domain.Release


case class DetailedSceneRelease(
  id: String,
  dirname: String,
  linkHref: String,

  time: Option[Long],
  groupName: Option[String],

  audioType: Option[String],
  videoType: Option[String],

  tvSeason: Option[Int],
  tvEpisode: Option[Int],

  numRatings: Int,
  videoRating: Float,
  audioRating: Float,

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

      numRatings = Some(numRatings),
      audioRating = Some(audioRating),
      videoRating = Some(videoRating),

      sizeInfo = size.map(_.toDomain),
      pubTime = time,
      extInfo = extInfo.map(_.toDomain),
      groupInfo = groupName.map(n => Group(id = "", name = Some(n)).toDomain)
    )
}
