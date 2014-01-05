package com.akuendig.movie.search.xrel

import com.akuendig.movie.domain


final case class ExtInfo(
  id: String,
  tpe: String,
  title: String,
  linkHref: Option[String] = None,
  uris: Set[String] = Set.empty,
  numRatings: Option[Int] = None,
  rating: Option[Double] = None
) {
  def toDomain: domain.ExtInfo = domain.ExtInfo(id, tpe, title, linkHref, uris, numRatings, rating)
}
