package com.akuendig.movie.search.xrel

import com.akuendig.movie.domain
import domain.intToPrimitive
import domain.floatToPrimitive


final case class ExtInfo(
  id: String,
  tpe: String,
  title: String,
  linkHref: Option[String] = None,
  uris: Set[String] = Set.empty,
  numRatings: Option[Int] = None,
  rating: Option[Float] = None
) {
  def toDomain: domain.ExtInfo = domain.ExtInfo(id, tpe, title, linkHref, uris, numRatings, rating)
}