package com.akuendig.movie.domain


final case class Category(
  var id: String= "",
  var metaCat: Option[String] = None,
  var subCat: Option[String] = None
)
