package com.akuendig.movie.domain


final case class Release(
  var id: String = "",

  var dirname: Option[String] = None,
  var linkHref: Option[String] = None,

  var mainLang: Option[String] = None,
  var pubTime: Option[Long] = None,

  var sizeInfo: Option[Size] = None,
  var groupInfo: Option[Group] = None,
  var extInfo: Option[ExtInfo] = None,
  var category: Option[Category] = None,

  var audioType: Option[String] = None,
  var videoType: Option[String] = None,

  var postTime: Option[Long] = None,
  var tvSeason: Option[Int] = None,
  var tvEpisode: Option[Int] = None,

  var numRatings: Option[Int] = None,
  var audioRating: Option[Float] = None,
  var videoRating: Option[Float] = None
)
