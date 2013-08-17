package com.akuendig.movie.search.xrel

import com.akuendig.movie.domain


final case class Size(
  number: Int = 0,
  unit: Option[String] = None
) {
  def toDomain: domain.Size = domain.Size(number, unit)
}
