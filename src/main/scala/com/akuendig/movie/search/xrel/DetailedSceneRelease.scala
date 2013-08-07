package com.akuendig.movie.search.xrel

import com.akuendig.movie.search.domain._
import com.akuendig.movie.search.domain.Group
import com.akuendig.movie.search.domain.Release
import com.akuendig.movie.search.domain.ExtInfo
import scala.Some


case class DetailedSceneRelease(
  id: String,
  dirname: String,
  linkHref: String,

  time: Long,
  groupName: String,

  audioType: Option[String],
  videoType: Option[String],

  tvSeason: Option[Int] = None,
  tvEpisode: Option[Int] = None,

  numRatings: Int,
  videoRating: Float,
  audioRating: Float,

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

      numRatings = Some(numRatings),
      audioRating = Some(audioRating),
      videoRating = Some(videoRating),

      size = size,
      pubTime = time,
      extInfo = extInfo,
      group = Group(id = "", name = groupName)
    )
}
