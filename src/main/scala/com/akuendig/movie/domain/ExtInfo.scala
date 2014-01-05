package com.akuendig.movie.domain


final case class ExtInfo(
  var id: String,
  var tpe: String,
  var title: String,
  var linkHref: Option[String] = None,
  var uris: Set[String] = Set.empty,
  var numRatings: Option[Int] = None,
  var rating: Option[Double] = None
)
