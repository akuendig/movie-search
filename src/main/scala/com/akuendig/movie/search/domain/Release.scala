package com.akuendig.movie.search.domain

/**
* Created with IntelliJ IDEA.
* User: adrian
* Date: 07.08.13
* Time: 21:18
* To change this template use File | Settings | File Templates.
*/
case class Release(
  id: String,
  dirname: String,
  linkHref: String,

  pubTime: Long,
  size: Size,

  group: Group,

  extInfo: ExtInfo,

  category: Option[Category] = None,
  mainLang: Option[String] = None,

  audioType: Option[String] = None,
  videoType: Option[String] = None,

  postTime: Option[Long] = None,
  tvSeason: Option[Int] = None,
  tvEpisode: Option[Int] = None,

  numRatings: Option[Int] = None,
  audioRating: Option[Float] = None,
  videoRating: Option[Float] = None
)
