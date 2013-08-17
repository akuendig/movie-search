package com.akuendig.movie.search.xrel

import com.akuendig.movie.domain


final case class Category(
  id: String,
  metaCat: Option[String] = None,
  subCat: Option[String] = None
) {
  def toDomain: domain.Category = domain.Category(id, metaCat, subCat)
}
