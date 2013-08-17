package com.akuendig.movie.search.xrel

import com.akuendig.movie.domain


final case class Group(
  id: String = "",
  name: Option[String] = None
) {
  def toDomain: domain.Group = domain.Group(id, name)
}
